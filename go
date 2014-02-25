#!/bin/bash

set -e

build() {
  bash -c "docker build -t docker-skydns github.com/nullstyle/docker-skydns"
  bash -c "cd app && gradle shadow && docker build -t services/app ."
  bash -c "cd ui && docker build -t services/ui ."
  bash -c "cd shell && docker build -t services/shell ."
}

run() {
  docker run -d -name skydns docker-skydns
  docker run -d -dns `dns-server` -link skydns:skydns -p 8080:8080 -name app services/app
  docker run -d -dns `dns-server` -link skydns:skydns -p 8081:80 -name ui services/ui
}

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

dig-skydns() {
  DNS_HOST=`dns-server`
  dig @$DNS_HOST $@
}

dns-server() {
  docker inspect skydns | grep IPAddress | sed 's/[^0-9\.]*//g'
}

shell() {
  docker run -t -i -dns `dns-server` services/shell
}

usage() {
  cat <<EOS
./go [subcommand]

	build		#Builds images
	run		#Builds and runs images in containers
	clean		#Stops and destroys containers
	dns-server	#Reports dns server
	dig-skydns	#Run dig against skydns docker container
	shell		#Run a bash shell with dns setup
EOS
}

if ! type $1 2>&1 | grep function > /dev/null; then
  usage
  exit 1
fi

$1 "${@:2}"

