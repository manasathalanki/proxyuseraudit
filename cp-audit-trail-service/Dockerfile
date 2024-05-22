FROM openjdk:latest
WORKDIR /cp-audit-trail-service
COPY /target/cp-audit-trail-service-1.0.0.jar /cp-audit-trail-service/
EXPOSE 8087
ENTRYPOINT ["java", "-Djasypt.encryptor.password=Winner@01","-jar", "/cp-audit-trail-service/cp-audit-trail-service-1.0.0.jar"]
