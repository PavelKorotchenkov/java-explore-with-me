FROM amazoncorretto:11-alpine-jdk

COPY service/target/*SNAPSHOT-exec.jar /app/service.jar
COPY client/target/*SNAPSHOT-exec.jar /app/client.jar
COPY common-dto/target/*SNAPSHOT-exec.jar /app/common-dto.jar

ENTRYPOINT ["java", "-jar", "app/service.jar"]

#COPY target/*.jar service.jar
# Копируем JAR файл сервиса в контейнер