#!/bin/bash

wk=`pwd`

function buildPkg() {
  if [ "$JAVA_17_HOME" != "" ];
  then
      echo $JAVA_17_HOME
  else
      echo "请配置JAVA_17_HOME环境变量"
      return;
  fi
  export JAVA_HOME=$JAVA_17_HOME
  echo "使用JAVA_HOME是:$JAVA_HOME"

  ./mvnw clean
  rm -rf target/
  ./mvnw package -Dmaven.test.skip=true -f pom.xml
  jarName=`ls target/*.jar|grep -v plain`
  echo $jarName
  if [ -z "${jarName}" ]; then
    echo "jar包没有正确编译"
    exit 1
  fi
  cp $jarName target/boot-chat.jar
  pkVersion='0.0.1-SNAPSHOT'
  echo "开始编译镜像:dockerhub.yonyougov.top/ligson/boot-chat:$pkVersion"
  docker buildx build --push --platform linux/amd64,linux/arm64 -t dockerhub.yonyougov.top/ligson/boot-chat:$pkVersion  --push   .
}


buildPkg
