package com.hytsnbr.shiny_test.constant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Store {
    
    AMAZON_CD(
        "Amazon CD",
        "Amazon CD"
    ),
    AMAZON_MUSIC(
        "Amazon Music",
        "Amazon Music (Streaming)"
    ),
    ANIMATE(
        "animate",
        "animate"
    ),
    ANIMELO_MIX(
        "animelo mix",
        "animelomix"
    ),
    APPLE_MUSIC(
        "Apple Music",
        "Apple Music"
    ),
    ASOBI_STORE(
        "アソビストア",
        "asobistore"
    ),
    AWA(
        "AWA",
        "AWA"
    ),
    A_ON_STORE(
        "A-on STORE",
        "A-on store"
    ),
    A_ON_STORE_POWERED_BY_ASMART(
        "A-on STORE Powered by A!SMART",
        "A-on STORE Powered by A!SMART"
    ),
    E_ONKYO_MUSIC(
        "e-onkyo music",
        "Eonkyo"
    
    ),
    GAMERS(
        "GAMERS",
        "GAMERS"
    ),
    HMV(
        "HMV&BOOKS online",
        "HMV Japan"
    ),
    ITUNES(
        "iTunes",
        "iTunes"
    ),
    LINE_MUSIC(
        "LINE MUSIC",
        "LINE MUSIC"
    ),
    MORA(
        "mora",
        "Mora"
    ),
    MORA_HIRES(
        "mora",
        "Mora 2"
    ),
    MUSIC_JP(
        "music.jp",
        "music.jp"
    ),
    RAKUTEN_BOOKS_JAPAN(
        "Rakuten ブックス",
        "Rakuten Books Japan"
    ),
    RECOCHOKU(
        "レコチョク",
        "RECOCHOKU"
    ),
    SEVEN_NET(
        "セブンネットショッピング",
        "7net"
    ),
    SOFMAP(
        "ソフマップ",
        "Sofmap"
    ),
    SPOTIFY(
        "Spotify",
        "Spotify"
    ),
    TORANOANA(
        "とらのあな",
        "Toranoana"
    ),
    TOWER_RECORDS_MUSIC(
        "TOWER RECORDS MUSIC",
        "TOWER RECORDS MUSIC"
    ),
    TOWER_RECORDS_ONLINE(
        "TOWER RECORDS ONLINE",
        "Tower Records Online"
    ),
    TSUTAYA(
        "TSUTAYA オンラインショッピング",
        "Tsutaya Online"
    ),
    YODOBASHI(
        "ヨドバシ.com",
        "Yodobashi"
    ),
    YOUTUBE_MUSIC(
        "Youtube Music",
        "YouTube Music"
    ),
    KKBOX(
        "KKBOX",
        "KKBOX"
    ),
    
    ;
    
    /** ストア名 */
    @Getter
    private final String name;
    
    /** HTMLから取得した際のストア名 */
    private final String htmlName;
    
    /**
     * HTMLから取得したストア名から逆引きする
     *
     * @param htmlName HTMLファイル中に記述されるストア名
     *
     * @return {@link Store}
     *
     * @throws IllegalArgumentException 合致するストアが見つからなかった場合
     */
    public static Store getByHtmlName(String htmlName) {
        return Arrays.stream(Store.values())
                     .filter(e -> StringUtils.equals(e.htmlName, htmlName))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid: " + htmlName));
    }
}
