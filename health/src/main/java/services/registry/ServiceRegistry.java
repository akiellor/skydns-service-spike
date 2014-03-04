package services.registry;

import com.google.common.collect.ImmutableList;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {
    public static class Service {
        private final String domain;
        private final int port;

        public Service(String domain, int port) {
            this.domain = domain;
            this.port = port;
        }

        public String getDomain() {
            return domain;
        }

        public int getPort() {
            return port;
        }
    }

    public static class Environment {
        private final String environment;
        private final List<Service> services;

        public Environment(String environment, List<Service> services) {
            this.environment = environment;
            this.services = services;
        }

        public Environment(String environment) {
            this(environment, ImmutableList.<Service>of());
        }

        public String getEnvironment() {
            return environment;
        }

        public List<Service> getServices() {
            return services;
        }
    }

    private final String domain;
    private final String dns;

    public ServiceRegistry(String dns, String domain) {
        this.dns = dns;
        this.domain = domain;
    }

    public Environment resolve(String environment) {
        Record[] records = null;
        try {
            Lookup lookup = new Lookup(environment + domain, Type.SRV);
            lookup.setResolver(new SimpleResolver(dns));
            records = lookup.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (records == null) {
            return new Environment(environment);
        } else {
            List<Service> services = new ArrayList<Service>();
            for (Record record : records) {
                int port = ((SRVRecord) record).getPort();
                String host = ((SRVRecord) record).getTarget().toString();
                services.add(new Service(host, port));
            }
            return new Environment(environment, services);
        }
    }
}
