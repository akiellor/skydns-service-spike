package services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

public class SkyDnsServiceConfiguration extends com.yammer.dropwizard.config.Configuration {
    @JsonProperty
    @NotBlank
    private String skyDnsHost;

    @JsonProperty
    @NotBlank
    private String skyDnsPort;

    public String getSkyDnsPort() {
        return skyDnsPort;
    }

    public String getSkyDnsHost() {
        return skyDnsHost;
    }
}

