package com.hytsnbr.shiny_colors.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** アプリ設定 */
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfig {

    /** データを強制更新するか */
    private final boolean isForce;

    /** バッチ処理内パラメータ名 */
    private final BatchJobParamNames batchJobParamNames;

    /** 対象URL */
    private final String targetUrl;

    /** JSONファイル生成先パス */
    private final String jsonDirPath;

    /** jsoup関連 */
    private final Jsoup jsoup;

    /** 処理関連 */
    private final Process process;

    /** コンストラクタ */
    public ApplicationConfig(
            boolean isForce,
            BatchJobParamNames batchJobParamNames,
            String targetUrl,
            String jsonDirPath,
            Jsoup jsoup,
            Process process) {
        this.isForce = isForce;
        this.batchJobParamNames = batchJobParamNames;
        this.targetUrl = targetUrl;
        this.jsonDirPath = jsonDirPath;
        this.jsoup = jsoup;
        this.process = process;
    }

    public boolean isForce() {
        return isForce;
    }

    public BatchJobParamNames getBatchJobParamNames() {
        return batchJobParamNames;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public String getJsonDirPath() {
        return this.jsonDirPath;
    }

    public Jsoup getJsoup() {
        return this.jsoup;
    }

    public Process getProcess() {
        return this.process;
    }

    /** バッチ処理内パラメータ名 */
    public static class BatchJobParamNames {

        /** バッチ処理日 */
        private final String executeDate;

        /** バッチ処理日時 */
        private final String executeDatetime;

        public BatchJobParamNames(String executeDate, String executeDatetime) {
            this.executeDate = executeDate;
            this.executeDatetime = executeDatetime;
        }

        public String getExecuteDate() {
            return this.executeDate;
        }

        public String getExecuteDatetime() {
            return this.executeDatetime;
        }
    }

    /** jsoup関連 */
    public static class Jsoup {

        /**
         * タイムアウト秒数（ミリ秒)<br>
         * デフォルト値: 30000（30秒）
         */
        private int timeout = 30000;

        public Jsoup(int timeout) {
            this.timeout = timeout;
        }

        public int getTimeout() {
            return this.timeout;
        }
    }

    /** 処理関連 */
    public static class Process {

        /**
         * ショップサイト取得時の再試行回数（回）<br>
         * デフォルト値: 5
         */
        private int retryLimit = 5;

        /**
         * ショップサイト取得時のクールタイム（ミリ秒）<br>
         * デフォルト値: 5000（秒）
         */
        private int coolTime = 5000;

        /** コンストラクタ */
        public Process(int retryLimit, int coolTime) {
            this.retryLimit = retryLimit;
            this.coolTime = coolTime;
        }

        public int getRetryLimit() {
            return this.retryLimit;
        }

        public int getCoolTime() {
            return this.coolTime;
        }
    }
}
