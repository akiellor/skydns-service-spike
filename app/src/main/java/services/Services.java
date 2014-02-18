package services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Services {
    public Services(List<Service> services) {
        this.services = services;
    }

    public Services() {
        this.services = ImmutableList.of();
    }

    @JsonProperty private List<Service> services;
}
