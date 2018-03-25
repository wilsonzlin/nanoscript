#!/usr/bin/env bash

set -e

pushd "$(dirname "$0")"

mvn clean compile assembly:single
cp target/nanoscript-*.jar .

popd

exit 0
