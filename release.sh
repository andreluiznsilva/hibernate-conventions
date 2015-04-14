#!/bin/bash

set -e

gradle clean build install

gradle release

git checkout mvn-repo

git reset --hard

git pull

cp -vr ~/.m2/repository/hibernate-conventions/. ./hibernate-conventions/

git add * -f

git commit -m "release"

git push

git checkout master
