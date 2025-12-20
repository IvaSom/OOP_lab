#!/bin/sh
set -e

echo "=== Starting PostgreSQL ==="

# Инициализируем базу данных если нужно
if [ ! -d "/var/lib/postgresql/data" ]; then
    mkdir -p /var/lib/postgresql/data
    chown postgres:postgres /var/lib/postgresql/data
    su-exec postgres initdb -D /var/lib/postgresql/data
fi

# Запускаем PostgreSQL
su-exec postgres pg_ctl start -D /var/lib/postgresql/data -l /var/lib/postgresql/logfile

# Ждем запуска PostgreSQL
echo "=== Waiting for PostgreSQL to start ==="
sleep 5

# Создаем базу данных если не существует
su-exec postgres psql -c "CREATE DATABASE laba5_test;" || true
su-exec postgres psql -c "CREATE USER postgres WITH PASSWORD '123456';" || true
su-exec postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE laba5_test TO postgres;" || true

echo "=== Starting Spring Boot Application ==="

# Запускаем приложение
exec java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.datasource.url=jdbc:postgresql://localhost:5432/laba5_test \
    -Dspring.datasource.username=postgres \
    -Dspring.datasource.password=123456 \
    -jar /app/app.jar