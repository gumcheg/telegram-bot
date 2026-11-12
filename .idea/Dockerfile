# Берем официальный образ с Java 17 (как у тебя в pom.xml)
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем Maven (чтобы собрать проект внутри контейнера)
RUN apk add --no-cache maven

# Копируем все файлы проекта в контейнер
WORKDIR /app
COPY . .

# Собираем проект (создаем тот самый .jar)
RUN mvn clean package -DskipTests

# Запускаем бота
CMD ["java", "-jar", "target/tg-bot-1.0-SNAPSHOT.jar"]
