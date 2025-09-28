package com.hytsnbr.shiny_colors.constant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/** ストア情報定数 */
public enum Store {











































    AMAZON_CD("Amazon CD", "Amazon CD", List.of()),

















    
    AMAZON_MUSIC("Amazon Music", "Amazon Music (Streaming)", List.of()),
    ANIMATE("animate", "animate", List.of()),
    ANIMELO_MIX("animelo mix", "animelomix", List.of("id")),
    APPLE_MUSIC("Apple Music", "Apple Music", List.of()),
    ASOBI_STORE("アソビストア", "asobistore", List.of()),
    AWA("AWA", "AWA", List.of()),
    A_ON_STORE("A-on STORE", "A-on store", List.of()),
    A_ON_STORE_POWERED_BY_ASMART(
            "A-on STORE Powered by A!SMART", "A-on STORE Powered by A!SMART", List.of("pid")),
    E_ONKYO_MUSIC("e-onkyo music", "Eonkyo", List.of()),
    GAMERS("GAMERS", "GAMERS", List.of()),
    HMV("HMV&BOOKS online", "HMV Japan", List.of()),
    ITUNES("iTunes", "iTunes", List.of()),
    LINE_MUSIC("LINE MUSIC", "LINE MUSIC", List.of("target", "item")),
    MORA("mora", "Mora", List.of()),
    MORA_HIRES("mora", "Mora 2", List.of()),
    MUSIC_JP("music.jp", "music.jp", List.of()),
    RAKUTEN_BOOKS_JAPAN("Rakuten ブックス", "Rakuten Books Japan", List.of()),
    RECOCHOKU("レコチョク", "RECOCHOKU", List.of()),
    SEVEN_NET("セブンネットショッピング", "7net", List.of()),
    SOFMAP("ソフマップ", "Sofmap", List.of()),
    SPOTIFY("Spotify", "Spotify", List.of()),
    TORANOANA("とらのあな", "Toranoana", List.of()),
    TOWER_RECORDS_MUSIC("TOWER RECORDS MUSIC", "TOWER RECORDS MUSIC", List.of()),
    TOWER_RECORDS_ONLINE("TOWER RECORDS ONLINE", "Tower Records Online", List.of()),
    TSUTAYA("TSUTAYA オンラインショッピング", "Tsutaya Online", List.of()),
    YODOBASHI("ヨドバシ.com", "Yodobashi", List.of()),
    YOUTUBE_MUSIC("Youtube Music", "YouTube Music", List.of()),
    KKBOX("KKBOX", "KKBOX", List.of()),
    ;

    /** ストア名 */
    private final String name;

    /** HTMLから取得した際のストア名 */
    private final String htmlName;

    /** URLに含むクエリパラメータ名 */
    private final List<String> includeQueryParams;

    /** コンストラクタ */
    Store(String name, String htmlName, List<String> includeQueryParams) {
        this.name = name;
        this.htmlName = htmlName;
        this.includeQueryParams = includeQueryParams;
    }

    /**
     * HTMLから取得したストア名から逆引きする
     *
     * @param htmlName HTMLファイル中に記述されるストア名
     * @return {@link Store}
     * @throws IllegalArgumentException 合致するストアが見つからなかった場合
     */
    public static Store getByHtmlName(String htmlName) {
        return Arrays.stream(Store.values())
                .filter(e -> StringUtils.equals(e.htmlName, htmlName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid: " + htmlName));
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIncludeQueryParams() {
        return this.includeQueryParams;
    }
}
