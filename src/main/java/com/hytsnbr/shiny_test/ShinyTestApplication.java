package com.hytsnbr.shiny_test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.hytsnbr.shiny_test.dto.CDInfo;
import com.hytsnbr.shiny_test.exception.SystemException;
import com.hytsnbr.shiny_test.service.FileOperator;
import com.hytsnbr.shiny_test.service.GenerateJson;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ShinyTestApplication implements CommandLineRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ShinyTestApplication.class);
    
    private final GenerateJson generateJson;
    
    private final FileOperator fileOperator;
    
    /** 強制更新 */
    @Value("${app-config.force}")
    private boolean isForce;
    
    /** コンストラクタ */
    public ShinyTestApplication(GenerateJson generateJson, FileOperator fileOperator) {
        this.generateJson = generateJson;
        this.fileOperator = fileOperator;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ShinyTestApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws SystemException {
        // 前回作成したファイルの作成日チェック
        if (this.isCreationDateToday()) {
            LOGGER.info("前回のファイル作成処理から日付が変わっていないため処理は中止されました");
            
            return;
        }
        
        var cdInfoList = this.generateJson.createCDInfoList();
        
        // 前回処理後のデータと一致する場合はファイル出力しない
        if (!this.matchPrevCDInfoList(cdInfoList)) {
            this.fileOperator.outputToJsonFile(cdInfoList);
            
            LOGGER.info("ファイルを作成しました");
        } else {
            LOGGER.info("前回処理時とデータ内容に変化がないためファイルは作成されませんでした");
        }
    }
    
    /**
     * 作成済みのファイルの作成日が現在日と一致するか<br>
     * 一致する場合：<code>true</code><br>
     * 一致しない場合、ファイルが存在しない・読み込みに失敗した場合：<code>false</code>
     */
    private boolean isCreationDateToday() {
        if (this.isForce) return false;
        
        var data = this.fileOperator.readJsonFile();
        if (Objects.isNull(data)) {
            return false;
        }
        
        final var createdAt = LocalDate.ofEpochDay(data.getCreatedAt());
        final var today = LocalDate.now();
        LOGGER.info("ファイル作成日: {}", createdAt);
        LOGGER.info("処理日: {}", today);
        
        return data.getCreatedAt() == today.toEpochDay();
    }
    
    /**
     * 前回処理時のデータと一致するか
     *
     * @param cdInfoList CD情報リスト
     *
     * @return 一致する場合は true
     */
    private boolean matchPrevCDInfoList(List<CDInfo> cdInfoList) {
        if (this.isForce) return false;
        
        var data = this.fileOperator.readJsonFile();
        if (Objects.isNull(data)) {
            return false;
        }
        
        for (var cdInfo : cdInfoList) {
            if (!data.getCdInfoList().contains(cdInfo)) {
                return false;
            }
        }
        
        return true;
    }
}
