FROM dockerhub.yonyougov.top/public/openjdk:17-ea-28-slim-buster-aliyun-font
WORKDIR /app
ADD entrypoint.sh /app/
COPY target/boot-chat.jar /app/
ENTRYPOINT ["sh","-c","/app/entrypoint.sh"]