#!/bin/bash
cd /app/ && java $JAVA_OPTS -Duser.timezone=Asia/Shanghai  -jar boot-chat.jar
