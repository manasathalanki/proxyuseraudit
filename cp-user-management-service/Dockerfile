FROM openjdk:latest
WORKDIR /cp-keycloak-wrapper-service
COPY /target/cp-user-management-service-1.0.0.jar /cp-user-management-service/
EXPOSE 9003
ENTRYPOINT ["java", "-Djasypt.encryptor.password=Winner@01", "-jar", "/cp-user-management-service/cp-user-management-service-1.0.0.jar"]
