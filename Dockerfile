# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# copy only pom first (dependency caching)
COPY pom.xml .

# download dependencies (cached layer)
RUN mvn -B -q -e -C dependency:go-offline

# now copy source
COPY src ./src

# build jar
RUN mvn -B clean package -DskipTests


# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# copy only jar (not source, not maven)
COPY --from=build /app/target/render-deploy.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
