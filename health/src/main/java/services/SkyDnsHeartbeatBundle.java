package services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class SkyDnsHeartbeatBundle implements ConfiguredBundle<SkyDnsServiceConfiguration> {
    private UUID id = UUID.randomUUID();

    public static class DnsHeartbeatJob implements Job {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                Environment environment = (Environment) checkNotNull(context.getJobDetail().getJobDataMap().get("environment"));
                SkyDnsServiceConfiguration configuration = (SkyDnsServiceConfiguration) checkNotNull(context.getJobDetail().getJobDataMap().get("configuration"));

                SkyDnsEntry entry = new SkyDnsEntry().ttl(20);

                HttpPatch patch = new HttpPatch();
                patch.setURI(getUri(environment, configuration));
                patch.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(entry)));

                CloseableHttpResponse result = null;
                try {
                    result = httpclient.execute(patch);
                    int statusCode = result.getStatusLine().getStatusCode();

                    if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        JobDetail job = newJob(DnsConnectJob.class)
                                .setJobData(new JobDataMap(ImmutableMap.of("configuration", configuration, "environment", environment)))
                                .build();

                        Trigger trigger = newTrigger()
                                .startNow()
                                .build();

                        context.getScheduler().scheduleJob(job, trigger);
                    } else if (statusCode != HttpURLConnection.HTTP_OK) {
                        String body = CharStreams.toString(new InputStreamReader(result.getEntity().getContent()));
                        throw new IOException("Unexpected HTTP response: " + statusCode + " " + body);
                    }
                } finally {
                    if(result != null) result.close();
                }
            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        }
    }

    public static class DnsConnectJob implements Job {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            if(context.getRefireCount() > 5) {
                throw new JobExecutionException("Max retry exceeded.");
            }

            try {
                Environment environment = (Environment) checkNotNull(context.getJobDetail().getJobDataMap().get("environment"));
                SkyDnsServiceConfiguration configuration = (SkyDnsServiceConfiguration) checkNotNull(context.getJobDetail().getJobDataMap().get("configuration"));

                SkyDnsEntry entry = new SkyDnsEntry()
                        .environment("production")
                        .name(environment.getName())
                        .host(InetAddress.getLocalHost().getHostAddress())
                        .port(8080)
                        .ttl(5);

                HttpPut put = new HttpPut();
                put.setURI(getUri(environment, configuration));
                put.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(entry)));

                CloseableHttpResponse result = null;
                try {
                    result = httpclient.execute(put);
                    int statusCode = result.getStatusLine().getStatusCode();

                    if (statusCode != HttpURLConnection.HTTP_CREATED) {
                        String body = CharStreams.toString(new InputStreamReader(result.getEntity().getContent()));
                        throw new IOException("Unexpected HTTP response: " + statusCode + " " + body);
                    }
                } finally {
                    if(result != null) result.close();
                }

                JobDetail job = newJob(DnsHeartbeatJob.class)
                        .setJobData(new JobDataMap(ImmutableMap.of("configuration", configuration, "environment", environment)))
                        .build();

                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(1)
                                .repeatForever())
                        .build();


                context.getScheduler().scheduleJob(job, trigger);
            } catch (Exception e) {
                throw new JobExecutionException(e, true);
            }
        }
    }

    private static URI getUri(Environment environment, SkyDnsServiceConfiguration configuration) throws URISyntaxException {
        return new URI("http://" + configuration.getSkyDnsHost() + ":" + configuration.getSkyDnsPort() + "/skydns/services/" + environment.getName());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(final SkyDnsServiceConfiguration configuration, final Environment environment) {

        environment.manage(new Managed() {
            private Scheduler scheduler;

            @Override
            public void start() throws Exception {
                scheduler = StdSchedulerFactory.getDefaultScheduler();

                scheduler.start();

                JobDetail job = newJob(DnsConnectJob.class)
                        .setJobData(new JobDataMap(ImmutableMap.of("configuration", configuration, "environment", environment)))
                        .build();

                Trigger trigger = newTrigger()
                        .startNow()
                        .build();

                scheduler.scheduleJob(job, trigger);
            }

            @Override
            public void stop() throws Exception {
                scheduler.shutdown();
            }
        });
    }
}
