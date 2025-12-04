# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# --- OPTİMİZASYON BURADA ---
# Önce sadece pom.xml'i kopyalıyoruz
COPY pom.xml .
# Kütüphaneleri indiriyoruz (Kaynak kod değişse bile burası Cache'ten gelir)
RUN mvn dependency:go-offline

# Şimdi kaynak kodu kopyalayıp derliyoruz
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (Burada değişiklik yok, gayet iyi)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]