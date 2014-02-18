package services;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/services")
public class ServicesResource {
    private final String dnsHost;

    public ServicesResource(String dnsHost) {
        this.dnsHost = dnsHost;
    }

    @Path("/{environment}/{serviceName}")
    @GET
    public Response services(@PathParam("environment") String environment,
                             @PathParam("serviceName") String serviceName) throws Exception {
        Lookup lookup = new Lookup(serviceName + "." + environment + ".skydns.local", Type.SRV);
        lookup.setResolver(new SimpleResolver(dnsHost));
        Record[] records = lookup.run();


        if(records == null) {
            return Response.status(200).type("application/json").entity(new Services()).build();
        } else {
            List<Service> services = new ArrayList<Service>();
            for(Record record : records) {
                services.add(new Service(((SRVRecord)record).getTarget().toString(), ((SRVRecord)record).getPort()));
            }
            return Response.status(200).type("application/json").entity(Optional.of(new Services(services))).build();
        }
    }
}
