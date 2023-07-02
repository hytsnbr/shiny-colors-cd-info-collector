package com.hytsnbr.shiny_test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hytsnbr.shiny_test.exception.SystemException;

@SpringBootApplication
public class ShinyTestApplication implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(ShinyTestApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        try {
            GenerateJson generateJson = new GenerateJson();
            generateJson.generate();
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }
}
