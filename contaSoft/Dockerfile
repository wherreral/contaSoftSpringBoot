FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY contaSoft/contaSoft-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
 "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
 "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", \
 "-Dspring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}", \
 "-Dspring.datasource.username=${DB_USER}", \
 "-Dspring.datasource.password=${DB_PASSWORD}", \
 "-Dspring.jpa.hibernate.ddl-auto=update", \
 "-jar", "app.jar"]