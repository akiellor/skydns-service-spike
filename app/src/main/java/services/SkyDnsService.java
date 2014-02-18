package services;

import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import static org.quartz.JobBuilder.newJob;

public class SkyDnsService extends com.yammer.dropwizard.Service<SkyDnsServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new SkyDnsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyDnsServiceConfiguration> bootstrap) {
        bootstrap.setName("service");

        bootstrap.addBundle(new SkyDnsHeartbeatBundle());
    }

    @Override
    public void run(SkyDnsServiceConfiguration configuration, final Environment environment) {
        environment.addResource(new ServicesResource(configuration.getSkyDnsHost()));
    }
}
