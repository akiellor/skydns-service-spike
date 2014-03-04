package services.doctor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import services.Health;

import java.util.List;

public class EnvironmentHealth {
    @JsonProperty private final String environment;
    @JsonProperty private final List<Health> services;

    public EnvironmentHealth(String environment, List<Health> services) {
        this.environment = environment;
        this.services = services;
    }

    public EnvironmentHealth(String environment) {
        this(environment, ImmutableList.<Health>of());
    }
}
