FROM amazoncorretto:11-alpine-jdk
COPY service/target/*.jar /app/service.jar
ENTRYPOINT ["java", "-jar", "app/service.jar"]
