package com.hytsnbr.shiny_test.constant;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Unit {
    
    SHINY_COLORS(
        "シャイニーカラーズ",
        "シャイニーカラーズ"
    ),
    ILLUMINATION_STARS(
        "illumination STARTS",
        "イルミネーションスターズ"
    ),
    LANTICA(
        "L'Antica",
        "アンティーカ"
    ),
    HO_KA_GO_CLIMAX_GIRLS(
        "放課後クライマックスガールズ",
        "放課後クライマックスガールズ"
    ),
    ALISTROEMERIA(
        "ALISTROEMERIA",
        "アルストロメリア"
    ),
    STRAYLIGHT(
        "Straylight",
        "ストレイライト"
    ),
    NOCTCHILL(
        "noctchill",
        "ノクチル"
    ),
    SHHIS(
        "SHHis",
        "シーズ"
    ),
    IKARUGA_LUCA(
        "斑鳩ルカ",
        "斑鳩ルカ"
    ),
    TEAM_STELLA(
        "Team.Stella",
        "Team.Stella"
    ),
    TEAM_LUNA(
        "Team.Luna",
        "Team.Luna"
    ),
    TEAM_SOL(
        "Team.Sol",
        "Team.Sol"
    ),
    FIVE_STARS(
        "THE IDOLM@STER FIVE STARS!!!!!",
        "THE IDOLM@STER FIVE STARS!!!!!"
    ),
    
    NONE("NONE", "NONE"),
    ;
    
    private final String name;
    
    private final String readingName;
    
    public static Unit getFromUnitName(String unitName) {
        return Arrays.stream(Unit.values())
                     .filter(e -> StringUtils.equals(e.name, unitName) || StringUtils.equals(e.readingName, unitName))
                     .findFirst()
                     .orElse(Unit.NONE);
    }
}
