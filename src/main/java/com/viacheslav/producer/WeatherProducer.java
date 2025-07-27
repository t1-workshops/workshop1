package com.viacheslav.producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viacheslav.consumer.adapter.LocalDateAdapter;
import com.viacheslav.model.WeatherData;
import org.apache.kafka.clients.producer.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WeatherProducer {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private static final String[] CITIES = {"Магадан", "Чукотка", "Питер", "Тюмень"};
    private static final String[] CONDITIONS = {"солнечно", "облачно", "дождь"};

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            while (true) {
                WeatherData data = generateRandomData();
                String json = gson.toJson(data);

                producer.send(new ProducerRecord<>("weather-topic", data.getCity(), json),
                        (metadata, exception) -> {
                            if (exception != null) {
                                System.err.println("Ошибка отправки: " + exception.getMessage());
                            } else {
                                System.out.println("Отправлено: " + json);
                            }
                        });

                TimeUnit.SECONDS.sleep(2); // Пауза 2 сек
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static WeatherData generateRandomData() {
        Random random = new Random();
        return new WeatherData(
                CITIES[random.nextInt(CITIES.length)],
                LocalDate.now().minusDays(random.nextInt(7)),
                random.nextInt(36), // 0-35°C
                CONDITIONS[random.nextInt(CONDITIONS.length)]
        );
    }
}
