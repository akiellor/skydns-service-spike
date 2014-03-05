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
