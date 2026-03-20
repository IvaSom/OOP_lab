package ru.ssau.tk.swc.labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType; // Добавь этот импорт

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.out.println("=== ПОПЫТКА ЗАПУСКА СЕРВЕРА ===");
        try {
            SpringApplication.run(Application.class, args);
            System.out.println("=== СЕРВЕР ЗАПУСТИЛСЯ И РАБОТАЕТ ===");
        } catch (Exception e) {
            System.out.println("=== ОШИБКА ПРИ ЗАПУСКЕ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}