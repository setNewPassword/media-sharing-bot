FROM amazoncorretto:11-alpine-jdk

COPY target/*.jar dispatcher.jar
ENTRYPOINT ["java","-jar","/dispatcher.jar"]