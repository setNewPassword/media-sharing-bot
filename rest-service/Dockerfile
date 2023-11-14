FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar rest-service.jar
ENTRYPOINT ["java","-jar","/rest-service.jar"]