#!/bin/bash

# Остановить процесс, занимающий порт 8099
fuser -k 8099/tcp

# Путь к JAR-файлу
JAR_PATH="/apps/juice_up/backend/target/juice-up-0.0.1-SNAPSHOT.jar"

# Запустить JAR-файл в фоновом режиме
nohup java -jar $JAR_PATH > /apps/juice_up/backend/target/juice_up.log 2>&1 &

echo "JAR-файл успешно перезапущен на порту 8099."