package services.doctor;

import services.Health;
import services.registry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

public class Doctor {
    public EnvironmentHealth check(ServiceRegistry.Environment environment) {
        List<Health> health = new ArrayList<Health>();
        for (ServiceRegistry.Service service : environment.getServices()) {
            health.add(new Health(service.getDomain(), service.getPort(), true));
        }
        return new EnvironmentHealth(environment.getEnvironment(), health);
    }
}
