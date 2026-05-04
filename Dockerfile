
# ── Stage 1 : Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copie pom.xml en premier pour profiter du cache Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Télécharge les dépendances (couche cachée)
RUN ./mvnw dependency:go-offline -q

# Copie le reste du code source
COPY src ./src

# Build le JAR final
RUN ./mvnw clean package -DskipTests -q

# ── Stage 2 : Run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copie uniquement le JAR depuis le stage builder
COPY --from=builder /app/target/*.jar app.jar

# Port exposé (doit correspondre à server.port dans application.properties)
EXPOSE 8080

# Variables d'environnement (peuvent être surchargées dans Render)
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
