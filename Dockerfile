#
# Build stage
#
FROM maven:3.6.1-jdk-8-alpine AS build
WORKDIR /app
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -Dmaven.test.skip=true clean package

#
# Package stage
#
FROM openjdk:8u212-jdk-alpine
COPY --from=build /app/target/product-service-0.0.1-SNAPSHOT.jar /usr/local/lib/product-service.jar
EXPOSE 8044
ENTRYPOINT ["java", "-jar","/usr/local/lib/product-service.jar"]