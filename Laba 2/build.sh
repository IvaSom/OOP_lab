#!/bin/bash

echo "1. Сборка WAR файла..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Ошибка сборки Maven!"
    exit 1
fi

echo "2. Проверка WAR файла..."
ls -la target/laba6-manual.war

echo "3. Сборка Docker образа..."
docker-compose build

echo "4. Запуск контейнеров..."
docker-compose up -d

echo "5. Проверка работы..."
sleep 10
curl -f http://localhost:8080/ || echo "Приложение еще не готово"