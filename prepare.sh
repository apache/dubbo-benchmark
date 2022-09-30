#!/usr/bin/env bash

DUBBO_VERSION=$1

echo "=====before replace====="
cat ./pom.xml

#get old version
OLD_VERSION=`awk '/<dubbo.version>[^<]+<\/dubbo.version>/{gsub(/<dubbo.version>|<\/dubbo.version>/,"",$1);print $1}' ./pom.xml | uniq`
s="s/<dubbo.version>$OLD_VERSION<\/dubbo.version>/<dubbo.version>$DUBBO_VERSION<\/dubbo.version>/g"
sed -i $s ./pom.xml

echo "=====after replace====="
cat ./pom.xml
