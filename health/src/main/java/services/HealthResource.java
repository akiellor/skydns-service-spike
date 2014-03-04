package services;

import com.google.common.base.Optional;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;
import services.doctor.Doctor;
import services.doctor.EnvironmentHealth;
import services.registry.ServiceRegistry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/health")
public class HealthResource {
    private final ServiceRegistry registry;
    private final Doctor doctor;

    public HealthResource(ServiceRegistry registry, Doctor doctor) {
        this.registry = registry;
        this.doctor = doctor;
    }

    @Path("/{environment}")
    @GET
    public Response services(@PathParam("environment") String environmentName) throws Exception {
        ServiceRegistry.Environment environment = registry.resolve(environmentName);
        EnvironmentHealth health = doctor.check(environment);

        return Response
                .status(200)
                .type("application/json")
                .entity(health)
                .build();
    }
}
