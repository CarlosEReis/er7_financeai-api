FROM amazoncorretto:21-al2023-headless
EXPOSE 8080
ARG JAR_FILE=target/financeai-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]