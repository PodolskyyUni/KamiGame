package org.example;

import javafx.application.Application;
import org.example.MainUI;
import org.example.SpringBootJavaFXApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Start Spring Boot first
        context = SpringApplication.run(Main.class, args);

        // Then launch JavaFX
        Application.launch(MainUI.class, args);

        // Ensure Spring shuts down when JavaFX exits
        context.close();
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}
