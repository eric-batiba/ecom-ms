FROM openjdk:21-oracle
ENV HOME=/app
COPY target/*.jar $HOME/app.jar
WORKDIR $HOME
ENTRYPOINT ["java", "-jar", "app.jar"]