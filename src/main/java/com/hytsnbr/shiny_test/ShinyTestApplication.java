package com.hytsnbr.shiny_test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hytsnbr.shiny_test.exception.SystemException;

@SpringBootApplication
public class ShinyTestApplication implements CommandLineRunner {
    
    private final GenerateJson generateJson;
    
    /** コンストラクタ */
    public ShinyTestApplication(GenerateJson generateJson) {
        this.generateJson = generateJson;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ShinyTestApplication.class, args);
    }
    
    @Override
    public void run(String... args) {
        try {
            this.generateJson.generate();
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }
}
