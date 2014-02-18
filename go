#!/bin/bash

set -e

build() {
  bash -c "docker build -t docker-skydns github.com/nullstyle/docker-skydns"
  bash -c "cd app && gradle shadow && docker build -t services/app ."
}

run() {
  docker run -d -p 53:53/udp -name skydns docker-skydns
  docker run -d -link skydns:skydns -p 8080:8080 -name app services/app
}

clean() {
  bash <<EOS
    docker stop app
    docker rm app
    docker stop skydns
    docker rm skydns
EOS
  git clean -df
}

usage() {
  cat <<EOS
./go [subcommand]

  build		#Builds images
  run		#Builds and runs images in containers
  clean		#Stops and destroys containers
EOS
}

case "$1" in
  build)
    build
    ;;
  run)
    build && run
    ;;
  clean)
    clean
    ;;
  *)
    usage
    ;;
esac

