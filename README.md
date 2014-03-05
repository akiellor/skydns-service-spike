Micro Services with Service Discovery via SkyDNS
------------------------------------------------

This is a spike to try out a [Micro Service]() architecture using [SkyDNS](https://github.com/skynetservices/skydns) for Service Registration and Discovery.

Getting Started
---------------

 * Assumes docker is installed on the host.
 * Assumes gradle is installed on the host.

```
$ ./go build
$ ./go run
$ ./go shell
$ dig *.production.skydns.local SRV #Prints services
$ curl http://service.production.skydns.local:8080/services/production/* #Rest API for listing services in environment
$ curl http://health.production.skydns.local:8080/health/production #Simple health check for entire environment
```
