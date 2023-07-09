package com.hytsnbr.shiny_test.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hytsnbr.shiny_test.config.ApplicationConfig;
import com.hytsnbr.shiny_test.dto.CDInfo;
import com.hytsnbr.shiny_test.dto.JsonData;
import com.hytsnbr.shiny_test.exception.SystemException;

/**
 * ファイル操作
 */
@Component
public class FileOperator {
    
    private static final ObjectMapper objectMapper;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperator.class);
    
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
    public JsonData readJsonFile() {
        try {
            var jsonFile = Paths.get(this.appConfig.getJsonPath()).toFile();
            return objectMapper.readValue(jsonFile, JsonData.class);
        } catch (FileNotFoundException e) {
            LOGGER.debug("生成ファイルが存在しません");
            
            return null;
        } catch (IOException e) {
            throw new SystemException("生成ファイルの読み込みに失敗しました", e);
        }
    }
    
    /**
     * JSONファイルに出力
     */
    public void outputToJsonFile(List<CDInfo> cdInfoList) {
        var path = Paths.get(this.appConfig.getJsonPath());
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new SystemException("ファイル作成に失敗しました", e);
        }
        
        try (var bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            var prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            var jsonText = objectMapper.writer(prettyPrinter).writeValueAsString(JsonData.of(cdInfoList));
            bufferedWriter.write(jsonText);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
}
