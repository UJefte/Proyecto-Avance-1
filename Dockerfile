# Etapa 1: compilar el proyecto con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# baja las dependencias primero para que Docker las guarde en cache
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Etapa 2: imagen ligera solo con el .jar
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=America/Mexico_City
COPY --from=build /app/target/condominios-acceso.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
