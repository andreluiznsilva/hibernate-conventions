#!/bin/bash

set -e

echo Select Release Version
read VERSION

gradle release -Pgradle.release.useAutomaticVersion=true -PreleaseVersion=$VERSION

git checkout $VERSION

gradle bintray

git checkout master

echo Ended Release $VERSION