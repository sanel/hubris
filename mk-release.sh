#!/bin/sh

version=`head -1 project.clj | awk '{print $3}' | sed 's/"//g'`
dir="hubris-$version"
dir_big="$dir-full"

lein uberjar

mkdir -p $dir_big
cp -R bin commands utils hubris-$version-standalone.jar *.md *.html $dir_big
tar -czpvf $dir_big.tar.gz $dir_big

mkdir -p $dir
cp -R bin commands utils hubris-$version.jar *.md *.html $dir
tar -czpvf $dir.tar.gz $dir

lein clean
rm -Rf $dir
rm -Rf $dir_big
