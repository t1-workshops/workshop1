# Weather Analytics with Kafka (workshop1)

Проект для сбора и анализа данных о погоде в реальном времени с использованием Apache Kafka. Позволяет генерировать случайные метеорологические данные и анализировать их для принятия решений о путешествиях.

## Запуск проекта

### Установка и настройка Kafka
```bash
# 1. Скачиваем и распаковываем Kafka 4.0.0
wget https://downloads.apache.org/kafka/4.0.0/kafka_2.13-4.0.0.tgz
tar -xzf kafka_2.13-4.0.0.tgz
cd kafka_2.13-4.0.0

# Генерируем уникальный Cluster ID
KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"

# Форматируем хранилище
bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/server.properties

# 3. Запускаем Kafka сервер
bin/kafka-server-start.sh config/server.properties
```

### Сборка и запуск приложения
```bash
git clone https://github.com/t1-workshops/workshop1.git
cd workshop1

# Сборка проекта
./gradlew build

# Запуск (в разных терминалах)
./gradlew runProducer  # Запуск продюсера
./gradlew runConsumer  # Запуск консьюмера

# Или запуск вместе
./gradlew run
```

## 🌡Примеры работы

### Вывод продюсера
```
Отправлено: {"city":"Магадан","date":"2025-07-28","temp":18,"condition":"солнечно"}
Отправлено: {"city":"Питер","date":"2025-07-27","temp":22,"condition":"дождь"}
Отправлено: {"city":"Тюмень","date":"2025-07-29","temp":30,"condition":"облачно"}
```

### Вывод консьюмера
```
=== Аналитика погоды ===
Магадан: 
  - Средняя температура: 15.3°C
  - Дождливых дней: 2
  - Максимальная температура: 25°C (2025-07-28)

Питер: 
  - Средняя температура: 18.7°C 
  - Дождливых дней: 5
  - Минимальная температура: 12°C (2025-07-25)
```
