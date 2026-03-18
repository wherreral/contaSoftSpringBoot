FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY contaSoft-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
 "--illegal-access=permit", \
 "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
 "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", \
 "--add-opens", "java.base/java.util=ALL-UNNAMED", \
 "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED", \
 "--add-opens", "java.base/java.io=ALL-UNNAMED", \
 "--add-opens", "java.base/sun.reflect=ALL-UNNAMED", \
 "--add-opens", "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED", \
 "-Dspring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}", \
 "-Dspring.datasource.username=${DB_USER}", \
 "-Dspring.datasource.password=${DB_PASSWORD}", \
 "-Dspring.jpa.hibernate.ddl-auto=update", \
 "-jar", "app.jar"]