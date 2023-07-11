#!/bin/sh
set -e

# first arg is `-f` or `--some-option`
# or first arg is `something.conf`
if [ "${1:0:1}" = '-' ]; then
    set -- tarantool "$@"
fi

# entry point wraps the passed script to do basic setup
if [ "$1" = 'tarantool' ]; then
    shift
    exec tarantool "/usr/local/bin/tarantool-entrypoint.lua" "$@"
fi

exec "$@"