#!/usr/bin/env bash

set -e

pushd "$(dirname "$0")"

mvn clean compile assembly:single

popd

exit 0
