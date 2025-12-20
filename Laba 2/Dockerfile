# Этап сборки
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только pom.xml сначала (для кэширования зависимостей)
COPY pom.xml .
# Скачиваем все зависимости
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Проверяем что JAR создался
RUN ls -la target/*.jar && echo "JAR файл создан успешно"

# Финальный образ
FROM eclipse-temurin:17-jre-alpine

# Рабочая директория
WORKDIR /app

# Копируем JAR файл из этапа сборки
# Используем wildcard чтобы найти любой JAR
COPY --from=builder /app/target/*.jar app.jar

# Проверяем что файл скопировался
RUN ls -la /app/ && echo "Файлы в /app:"

# Открываем порт
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]