FROM openjdk:8
ADD target/host-status.jar host-status.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "host-status.jar"]
