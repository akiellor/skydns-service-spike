package services;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SkyDnsEntry {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("Environment")
    private String environment;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("Host")
    private String host;

    @JsonProperty("Port")
    private int port;

    @JsonProperty("TTL")
    private int ttl;

    public services.SkyDnsEntry name(String name) {
        this.name = name;
        return this;
    }

    public services.SkyDnsEntry version(String version) {
        this.version = version;
        return this;
    }

    public services.SkyDnsEntry environment(String environment) {
        this.environment = environment;
        return this;
    }

    public services.SkyDnsEntry region(String region) {
        this.region = region;
        return this;
    }

    public services.SkyDnsEntry host(String host) {
        this.host = host;
        return this;
    }

    public services.SkyDnsEntry port(int port) {
        this.port = port;
        return this;
    }

    public services.SkyDnsEntry ttl(int ttl) {
        this.ttl = ttl;
        return this;
    }
}
