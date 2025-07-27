package com.viacheslav;

import com.viacheslav.consumer.WeatherConsumer;
import com.viacheslav.producer.WeatherProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("=== Запуск погодного сервиса ===");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                WeatherProducer.main(new String[]{});
            } catch (Exception e) {
                System.err.println("Ошибка в продюсере: " + e.getMessage());
            }
        });

        executor.submit(() -> {
            try {
                WeatherConsumer.main(new String[]{});
            } catch (Exception e) {
                System.err.println("Ошибка в консьюмере: " + e.getMessage());
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n=== Завершение работы ===");
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    System.err.println("Потоки не завершились корректно");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}