#!/bin/sh

set -e

cat > /opt/config.yaml <<EOS
skyDnsHost: $SKYDNS_PORT_8080_TCP_ADDR
skyDnsPort: $SKYDNS_PORT_8080_TCP_PORT
EOS

java -cp /opt/app.jar services.SkyDnsService server /opt/config.yaml
