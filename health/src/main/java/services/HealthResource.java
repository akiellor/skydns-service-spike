package services;

import com.google.common.base.Optional;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/health")
public class HealthResource {
    private final String dnsHost;

    public HealthResource(String dnsHost) {
        this.dnsHost = dnsHost;
    }

    @Path("/{environment}")
    @GET
    public Response services(@PathParam("environment") String environment) throws Exception {
        Lookup lookup = new Lookup(environment + ".skydns.local", Type.SRV);
        lookup.setResolver(new SimpleResolver(dnsHost));
        Record[] records = lookup.run();


        if(records == null) {
            return Response.status(200).type("application/json").entity(new EnvironmentHealth(environment)).build();
        } else {
            List<Health> services = new ArrayList<Health>();
            for(Record record : records) {
                int port = ((SRVRecord) record).getPort();
                String host = ((SRVRecord) record).getTarget().toString();
                boolean ok = true;
                services.add(new Health(host, port, ok));
            }
            return Response.status(200).type("application/json").entity(Optional.of(new EnvironmentHealth(environment, services))).build();
        }
    }
}
