package services.doctor;

import services.Health;
import services.registry.ServiceRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Doctor {
    public EnvironmentHealth check(ServiceRegistry.Environment environment) {
        List<Health> health = new ArrayList<Health>();
        for (ServiceRegistry.Service service : environment.getServices()) {
            int port = service.getPort();
            String domain = service.getDomain();

            boolean ok = check(domain, port);

            health.add(new Health(domain, port, ok));
        }
        return new EnvironmentHealth(environment.getEnvironment(), health);
    }

    private boolean check(String domain, int port) {
        Socket socket = new Socket();
        boolean ok = true;
        try {
            socket.connect(new InetSocketAddress(domain, port));
            ok = socket.isConnected();
        } catch (IOException e) {
            ok = false;
        } finally {
            if(socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    ok = false;
                }
            }
        }
        return ok;
    }
}
