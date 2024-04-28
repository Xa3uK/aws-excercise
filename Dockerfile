FROM --platform=linux/arm64 eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/aws-exercise-0.0.1-SNAPSHOT.jar /app/aws-exercise-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "aws-exercise-0.0.1-SNAPSHOT.jar"]
