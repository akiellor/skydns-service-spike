#!/bin/bash

set -e

build() {
  bash -c "docker build -t docker-skydns github.com/nullstyle/docker-skydns"
  bash -c "cd app && gradle shadow && docker build -t services/app ."
  bash -c "cd ui && docker build -t services/ui ."
}

run() {
  docker run -d -p 53:53/udp -name skydns docker-skydns
  docker run -d -link skydns:skydns -p 8080:8080 -name app services/app
  docker run -d -link skydns:skydns -p 8081:80 -name ui services/ui
}

clean() {
  bash <<EOS
    docker stop ui
    docker rm ui
    docker stop app
    docker rm app
    docker stop skydns
    docker rm skydns
EOS
  git clean -df
}

dig-skydns() {
  DNS_HOST=$(docker inspect skydns | grep IPAddress | sed 's/[^0-9\.]*//g')
  dig @$DNS_HOST $@
}

usage() {
  cat <<EOS
./go [subcommand]

  build		#Builds images
  run		#Builds and runs images in containers
  clean		#Stops and destroys containers
  dig-skydns    #Run dig against skydns docker container
EOS
}

if ! type $1 2>&1 | grep function > /dev/null; then
  usage
  exit 1
fi

$1 "${@:2}"

