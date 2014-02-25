package services;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Health {
    @JsonProperty private String host;
    @JsonProperty private int port;
    @JsonProperty private final boolean ok;

    public Health(String host, int port, boolean ok) {
        this.ok = ok;
        this.host = host.replaceAll("\\.*$", "");
        this.port = port;
    }
}
