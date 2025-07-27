package com.viacheslav.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viacheslav.consumer.adapter.LocalDateAdapter;
import com.viacheslav.model.WeatherData;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class WeatherConsumer {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private static final Map<String, List<Integer>> cityTemps = new HashMap<>();
    private static final Map<String, Integer> rainyDays = new HashMap<>();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "weather-analytics");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton("weather-topic"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    WeatherData data = parseWeatherData(record.value());
                    if (data != null) {
                        updateStats(data);
                        printStats();
                    }
                }
            }
        }
    }

    private static WeatherData parseWeatherData(String json) {
        try {
            return gson.fromJson(json, WeatherData.class);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
            System.err.println("Проблемное сообщение: " + json);
            return null;
        }
    }

    private static void updateStats(WeatherData data) {
        cityTemps.computeIfAbsent(data.getCity(), k -> new ArrayList<>())
                .add(data.getTemperature());

        if ("дождь".equals(data.getCondition())) {
            rainyDays.merge(data.getCity(), 1, Integer::sum);
        }
    }

    private static void printStats() {
        System.out.println("\n=== Аналитика погоды ===");
        cityTemps.forEach((city, temps) -> {
            double avgTemp = temps.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            System.out.printf("%s: Средняя температура: %.1f°C, Дождливых дней: %d\n",
                    city, avgTemp, rainyDays.getOrDefault(city, 0));
        });
    }
}