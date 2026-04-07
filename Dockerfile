FROM eclipse-temurin:17-jdk-alpine-3.20
WORKDIR /app

# Install the application dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

# Copy in the source code
COPY src ./src
EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]