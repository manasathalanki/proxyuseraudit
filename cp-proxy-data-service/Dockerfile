FROM openjdk:latest
WORKDIR /cp-proxy-data-service
COPY /target/cp-proxy-data-service-1.0.0.jar /cp-proxy-data-service/
EXPOSE 8086
ENTRYPOINT ["java", "-Djasypt.encryptor.password=Winner@01", "-jar", "/cp-proxy-data-service/cp-proxy-data-service-1.0.0.jar"]
