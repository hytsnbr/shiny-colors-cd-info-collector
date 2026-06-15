package com.hytsnbr.shiny_colors.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    /* staticイニシャライザー */
    static {
        objectMapper = new ObjectMapper();
        // Jacksonで Java8 の LocalDate 関係を処理できるようにする
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** コンストラクタ */
    public FileOperator(ApplicationConfig appConfig) {}

    /**
     * JSONファイルを読み取ってオブジェクトで返却する
     *
     * @param jsonFilePath 読み込み対象JSONファイルパス
     * @throws SystemException JSONファイル読み込みに失敗した場合
     */
    public <T> T readJsonFile(Path jsonFilePath, Class<T> clazz) throws SystemException {
        try {
            var jsonFile = jsonFilePath.toFile();
            return objectMapper.readValue(jsonFile, clazz);
        } catch (FileNotFoundException e) {
            logger.info("生成ファイルが存在しません");

            return null;
        } catch (IOException e) {
            throw new SystemException("生成ファイルの読み込みに失敗しました", e);
        }
    }

    /**
     * データをJSONファイルに出力する
     *
     * @param jsonData 出力対象データ
     * @param jsonFileName データ出力先JSONファイル名
     * @throws SystemException データ出力に失敗した場合
     */
    public void outputToJsonFile(Object jsonData, String jsonFileName) throws SystemException {
        var path = Paths.get(jsonFileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new SystemException("ファイル作成に失敗しました", e);
        }

        try (var bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            var jsonText = objectMapper.writeValueAsString(jsonData);
            var json =
                    new GsonBuilder()
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
