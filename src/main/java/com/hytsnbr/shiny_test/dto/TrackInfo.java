package com.hytsnbr.shiny_test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 楽曲情報 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackInfo {
    
    /** 楽曲名 */
    private String trackName;
    
    /** トラックNo */
    private int trackNo;
    
    /** オフボーカル版か */
    private boolean isOffVocal;
}