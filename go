#!/bin/bash

set -e

### container-kill: Stops and removes container with specified name.
container-kill() {
  docker stop $1
  docker rm $1
}

### build: Builds images
build() {
  bash -c "docker build -t docker-skydns github.com/nullstyle/docker-skydns"
  bash -c "cd app && gradle shadow && docker build -t services/app ."
  bash -c "cd health && gradle shadow && docker build -t services/health ."
  bash -c "cd ui && docker build -t services/ui ."
  bash -c "cd shell && docker build -t services/shell ."
}

### run: Builds and runs images in containers
run() {
  docker run -d -name skydns docker-skydns
  docker run -d -dns `dns-server` -link skydns:skydns -p 8080:8080 -name app services/app
  docker run -d -dns `dns-server` -link skydns:skydns -p 8081:80 -name ui services/ui
  docker run -d -dns `dns-server` -link skydns:skydns -p 8082:8080 -name health services/health
}

### clean: Stops and destroys containers
clean() {
  bash <<EOS
    docker stop ui
    docker rm ui
    docker stop app
    docker rm app
    docker stop skydns
    docker rm skydns
    docker stop health
    docker rm health
EOS
}

### dig-skydns: Run dig against skydns docker container
dig-skydns() {
  DNS_HOST=`dns-server`
  dig @$DNS_HOST $@
}

### dns-server: Reports dns server
dns-server() {
  docker inspect skydns | grep IPAddress | sed 's/[^0-9\.]*//g'
}

### shell: Run a bash shell with dns setup
shell() {
  docker run -t -i -dns `dns-server` services/shell
}

### usage: Prints this usage information
usage() {
  cat <<EOS
$0 [subcommand]
EOS

grep -e "^###" $0 | sed -rn 's/###\s*(.*?): (.*)/\t\1\t\t#\2/p' | sort
}

if ! type $1 2>&1 | grep function > /dev/null; then
  usage
  exit 1
fi

$1 "${@:2}"

