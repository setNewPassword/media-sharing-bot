FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar node.jar
ENTRYPOINT ["java","-jar","/node.jar"]