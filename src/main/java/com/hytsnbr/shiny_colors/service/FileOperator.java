package com.hytsnbr.shiny_colors.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.exception.SystemException;

/** ファイル操作サービス */
@Component
public class FileOperator {
    
    private static final ObjectMapper objectMapper;
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(FileOperator.class);
    
    private final ApplicationConfig appConfig;
    
    /* staticイニシャライザー */
    static {
        objectMapper = new ObjectMapper();
        // Jacksonで Java8 の LocalDate 関係を処理できるようにする
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /** コンストラクタ */
    public FileOperator(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    /**
     * JSONファイルを読み取ってオブジェクトで返却する
     */
    public <T> T readJsonFile(final String jsonFileName, Class<T> clazz) {
        try {
            var jsonFile = Paths.get(this.appConfig.getJsonDirPath(), jsonFileName).toFile();
            return objectMapper.readValue(jsonFile, clazz);
        } catch (FileNotFoundException e) {
            logger.debug("生成ファイルが存在しません");
            
            return null;
        } catch (IOException e) {
            throw new SystemException("生成ファイルの読み込みに失敗しました", e);
        }
    }
    
    /**
     * JSONファイルに出力
     */
    public void outputToJsonFile(Object jsonData, String jsonFileName) {
        var path = Paths.get(this.appConfig.getJsonDirPath(), jsonFileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new SystemException("ファイル作成に失敗しました", e);
        }
        
        try (var bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            var jsonText = objectMapper.writeValueAsString(jsonData);
            var json = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(JsonParser.parseString(jsonText));
            bufferedWriter.write(json);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
}
