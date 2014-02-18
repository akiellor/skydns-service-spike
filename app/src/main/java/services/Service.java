package services;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
    public Service(String host, int port) {
        this.host = host.replaceAll("\\.*$", "");
        this.port = port;
    }

    @JsonProperty private String host;

    @JsonProperty private int port;
}
