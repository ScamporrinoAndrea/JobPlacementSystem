FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

#Copy entire project directory to Docker context
COPY . .

#Copy the JAR file
COPY build/libs/*.jar app.jar

#EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]