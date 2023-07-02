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
            List.of("https://www.amazon.co.jp"),
            false
    ),
    AMAZON_MUSIC(
            "Amazon Music",
            List.of("https://music.amazon.co.jp"),
            false
    ),
    ANIMATE(
            "animate",
            List.of("https://www.animate-onlineshop.jp"),
            false
    ),
    ANIMELO_MIX(
            "animelo mix",
            List.of("https://r.animelo.jp", "https://pc.animelo.jp"),
            false
    ),
    APPLE_MUSIC(
            "Apple Music",
            List.of("https://music.apple.com"),
            false
    ),
    ASOBI_STORE(
            "アソビストア",
            List.of("https://shop.asobistore.jp"),
            false
    ),
    AWA(
            "AWA",
            List.of("https://s.awa.fm", "https://mf.awa.fm"),
            false
    ),
    A_ON_STORE(
            "A-on STORE",
            List.of("https://a-onstore.jp"),
            false
    ),
    A_ON_STORE_POWERED_BY_ASMART(
            "A-on STORE Powered by A!SMART",
            List.of("https://www.asmart.jp"),
            false
    ),
    E_ONKYO_MUSIC(
            "e-onkyo music",
            List.of("https://www.e-onkyo.com"),
            true
    ),
    GAMERS(
            "GAMERS",
            List.of("https://www.gamers.co.jp"),
            false
    ),
    HMV(
            "HMV&BOOKS online",
            List.of("https://www.hmv.co.jp"),
            false
    ),
    ITUNES(
            "iTunes",
            List.of("https://music.apple.com"),
            false
    ),
    LINE_MUSIC(
            "LINE MUSIC",
            List.of("https://music.line.me", "https://lin.ee"),
            false
    ),
    MORA(
            "mora",
            List.of("https://mora.jp"),
            false
    ),
    MORA_HIRES(
            "mora",
            List.of("https://mora.jp"),
            true
    ),
    MUSIC_JP(
            "music.jp",
            List.of("https://music-book.jp"),
            false
    ),
    RAKUTEN_BOOKS_JAPAN(
            "Rakuten ブックス",
            List.of("https://books.rakuten.co.jp"),
            false
    ),
    RECOCHOKU(
            "レコチョク",
            List.of("https://recochoku.jp"),
            false
    ),
    SEVEN_NET(
            "セブンネットショッピング",
            List.of("https://7net.omni7.jp"),
            false
    ),
    SOFMAP(
            "ソフマップ",
            List.of("https://a.sofmap.com"),
            false
    ),
    SPOTIFY(
            "Spotify",
            List.of("https://open.spotify.com"),
            false
    ),
    TORANOANA(
            "とらのあな",
            List.of("https://ecs.toranoana.jp"),
            false
    ),
    TOWER_RECORDS_MUSIC(
            "TOWER RECORDS MUSIC",
            List.of("https://music.tower.jp"),
            false
    ),
    TOWER_RECORDS_ONLINE(
            "TOWER RECORDS ONLINE",
            List.of("https://tower.jp"),
            false
    ),
    TSUTAYA(
            "TSUTAYA オンラインショッピング",
            List.of("https://shop.tsutaya.co.jp"),
            false
    ),
    YODOBASHI(
            "ヨドバシ.com",
            List.of("https://www.yodobashi.com"),
            false
    ),
    YOUTUBE_MUSIC(
            "Youtube Music",
            List.of("https://music.youtube.com"),
            false
    ),
    ;
    
    private final String storeName;
    
    private final List<String> domainList;
    
    private final boolean isHiRes;
    
    public static Store getByDomain(String url) {
        return Arrays.stream(Store.values())
                     .filter(e -> {
                         var httpsUrl = url.replace("http://", "https://");
                         return StringUtils.containsAny(httpsUrl, e.domainList.toArray(new String[0]));
                     })
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid URL: " + url));
    }
}
