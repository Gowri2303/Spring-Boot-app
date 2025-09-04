package com.spring.springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App implements CommandLineRunner {

    private final WebhookService webhookService;

    // Constructor injection
    public App(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("App started, running webhook flow...");
        webhookService.executeWebhookFlow();
    }

    // RestTemplate bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
