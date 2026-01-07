FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/nexia-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV NEXIA_CORE_URL=http://nexia-core:8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
