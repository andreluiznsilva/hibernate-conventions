#!/bin/bash

gradle clean build install

gradle release

gradle clean build install

git checkout mvn-repo

git reset --hard

git pull

cp -vr ~/.m2/repository/hibernate-conventions/. ./hibernate-conventions/

git add * -f

git commit -a

git push

git checkout master

git reset --hard
