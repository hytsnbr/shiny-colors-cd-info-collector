package com.hytsnbr.shiny_colors.constant;

/** 生成JSONファイル名定義 */
public final class JsonFileName {

    /** data.json */
    public static final String DATA_JSON = "data.json";

    /** DiscographyList.json */
    public static final String DISCOGRAPHY_LIST_JSON = "DiscographyList.json";

    /** CDInfoList.json */
    public static final String CD_INFO_LIST_JSON = "CdInfoList.json";

    private JsonFileName() {
        throw new IllegalStateException("このクラスはインスタンス化できません");
    }
}
