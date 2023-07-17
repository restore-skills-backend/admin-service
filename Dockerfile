FROM openjdk:17
 ENV SPRING_PROFILES_ACTIVE=dev
 ADD target/admin-service.jar admin-service.jar
 EXPOSE 8083
ENTRYPOINT ["java","-jar","admin-service.jar"]