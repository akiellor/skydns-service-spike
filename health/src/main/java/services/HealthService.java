package services;

import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import static org.quartz.JobBuilder.newJob;

public class HealthService extends com.yammer.dropwizard.Service<SkyDnsServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new HealthService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyDnsServiceConfiguration> bootstrap) {
        bootstrap.setName("health");

        bootstrap.addBundle(new SkyDnsHeartbeatBundle());
    }

    @Override
    public void run(SkyDnsServiceConfiguration configuration, final Environment environment) {
        environment.addResource(new HealthResource(configuration.getSkyDnsHost()));
    }
}
