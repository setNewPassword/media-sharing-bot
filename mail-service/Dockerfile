FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar mail-service.jar
ENTRYPOINT ["java","-jar","/mail-service.jar"]