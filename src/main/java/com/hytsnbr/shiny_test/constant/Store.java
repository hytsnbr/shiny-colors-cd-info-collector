package com.hytsnbr.shiny_test.constant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Store {
    AMAZON_CD(
        "Amazon CD",
        List.of("www.amazon.co.jp"),
        false
    ),
    AMAZON_MUSIC(
        "Amazon Music",
        List.of("music.amazon.co.jp"),
        false
    ),
    ANIMATE(
        "animate",
        List.of("www.animate-onlineshop.jp"),
        false
    ),
    ANIMELO_MIX(
        "animelo mix",
        List.of("r.animelo.jp", "pc.animelo.jp"),
        false
    ),
    APPLE_MUSIC(
        "Apple Music",
        List.of("music.apple.com"),
        false
    ),
    ASOBI_STORE(
        "アソビストア",
        List.of("shop.asobistore.jp"),
        false
    ),
    AWA(
        "AWA",
        List.of("s.awa.fm", "mf.awa.fm"),
        false
    ),
    A_ON_STORE(
        "A-on STORE",
        List.of("a-onstore.jp"),
        false
    ),
    A_ON_STORE_POWERED_BY_ASMART(
        "A-on STORE Powered by A!SMART",
        List.of("www.asmart.jp"),
        false
    ),
    E_ONKYO_MUSIC(
        "e-onkyo music",
        List.of("www.e-onkyo.com"),
        true
    ),
    GAMERS(
        "GAMERS",
        List.of("www.gamers.co.jp"),
        false
    ),
    HMV(
        "HMV&BOOKS online",
        List.of("www.hmv.co.jp"),
        false
    ),
    ITUNES(
        "iTunes",
        List.of("music.apple.com"),
        false
    ),
    LINE_MUSIC(
        "LINE MUSIC",
        List.of("music.line.me", "lin.ee"),
        false
    ),
    MORA(
        "mora",
        List.of("mora.jp"),
        false
    ),
    MORA_HIRES(
        "mora",
        List.of("mora.jp"),
        true
    ),
    MUSIC_JP(
        "music.jp",
        List.of("music-book.jp"),
        false
    ),
    RAKUTEN_BOOKS_JAPAN(
        "Rakuten ブックス",
        List.of("books.rakuten.co.jp"),
        false
    ),
    RECOCHOKU(
        "レコチョク",
        List.of("recochoku.jp"),
        false
    ),
    SEVEN_NET(
        "セブンネットショッピング",
        List.of("7net.omni7.jp"),
        false
    ),
    SOFMAP(
        "ソフマップ",
        List.of("a.sofmap.com"),
        false
    ),
    SPOTIFY(
        "Spotify",
        List.of("open.spotify.com"),
        false
    ),
    TORANOANA(
        "とらのあな",
        List.of("ecs.toranoana.jp"),
        false
    ),
    TOWER_RECORDS_MUSIC(
        "TOWER RECORDS MUSIC",
        List.of("music.tower.jp"),
        false
    ),
    TOWER_RECORDS_ONLINE(
        "TOWER RECORDS ONLINE",
        List.of("tower.jp"),
        false
    ),
    TSUTAYA(
        "TSUTAYA オンラインショッピング",
        List.of("shop.tsutaya.co.jp"),
        false
    ),
    YODOBASHI(
        "ヨドバシ.com",
        List.of("www.yodobashi.com"),
        false
    ),
    YOUTUBE_MUSIC(
        "Youtube Music",
        List.of("music.youtube.com"),
        false
    ),
    ;
    
    private final String storeName;
    
    private final List<String> domainList;
    
    private final boolean isHiRes;
    
    public static Store getByDomain(String url) {
        return Arrays.stream(Store.values())
                     .filter(e -> StringUtils.containsAny(url, e.domainList.toArray(new String[0])))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid URL: " + url));
    }
}
