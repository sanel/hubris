#!/bin/sh

version=`head -1 project.clj | awk '{print $3}' | sed 's/"//g'`
dir="hubris-$version"

lein uberjar

mkdir -p $dir
cp -R bin commands utils hubris-$version-standalone.jar *.md *.html $dir
tar -czpvf $dir.tar.gz $dir

lein clean
rm -Rf $dir
