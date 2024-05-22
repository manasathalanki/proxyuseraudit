FROM openjdk:latest
WORKDIR /cp-dashboard-configuration-service
COPY /target/cp-dashboard-configuration-service-1.0.0.jar /cp-dashboard-configuration-service/
EXPOSE 8085
ENTRYPOINT ["java", "-Djasypt.encryptor.password=Winner@01","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/cp-dashboard-configuration-service/heapdump.bin","-jar", "/cp-dashboard-configuration-service/cp-dashboard-configuration-service-1.0.0.jar"]
