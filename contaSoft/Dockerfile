FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY contaSoft-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}", \
  "-Dspring.datasource.username=${DB_USER}", \
  "-Dspring.datasource.password=${DB_PASSWORD}", \
  "-Dspring.jpa.hibernate.ddl-auto=update", \
  "-jar", "app.jar"]