package com.hytsnbr.shiny_colors.constant;

/** ステップ名定義 */
public final class StepName {

    /** ディスコグラフィー情報作成ステップ */
    public static final String CREATE_DISCOGRAPHY_LIST_STEP = "ディスコグラフィー情報作成ステップ";

    /** CD情報リスト作成ステップ */
    public static final String CREATE_CD_INFO_LIST_STEP = "CD情報リスト作成ステップ";

    /** JSONデータ作成ステップ */
    public static final String GENERATE_DATA_JSON_STEP = "JSONデータ作成ステップ";

    /** クリーンアップ処理ステップ */
    public static final String CLEANUP_STEP = "クリーンアップ処理ステップ";

    private StepName() {
        throw new IllegalStateException("このクラスはインスタンス化できません");
    }
}
