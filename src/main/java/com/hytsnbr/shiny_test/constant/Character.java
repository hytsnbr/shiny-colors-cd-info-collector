package com.hytsnbr.shiny_test.constant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Character {
    
    /**  */
    AMANA("大崎甘奈", "黒木ほの香", List.of(Unit.ALISTROEMERIA, Unit.TEAM_STELLA)),
    /**  */
    ASAHI("芹沢あさひ", "田中有紀", List.of(Unit.STRAYLIGHT, Unit.TEAM_STELLA)),
    /**  */
    CHIYOKO("園田智代子", "白石晴香", List.of(Unit.HO_KA_GO_CLIMAX_GIRLS, Unit.TEAM_STELLA)),
    /**  */
    CHIYUKI("桑山千雪", "芝崎典子", List.of(Unit.ALISTROEMERIA, Unit.TEAM_SOL)),
    /**  */
    FUYUKO("黛冬優子", "幸村恵理", List.of(Unit.STRAYLIGHT, Unit.TEAM_SOL)),
    /**  */
    HINANA("市川雛菜", "岡咲美保", List.of(Unit.NOCTCHILL, Unit.TEAM_SOL)),
    /**  */
    HIORI("風野灯織", "近藤玲奈", List.of(Unit.ILLUMINATION_STARS, Unit.TEAM_LUNA)),
    /**  */
    JURI("西城樹里", "永井真里子", List.of(Unit.HO_KA_GO_CLIMAX_GIRLS, Unit.TEAM_SOL)),
    /**  */
    KAHO("小宮果穂", "河野ひより", List.of(Unit.HO_KA_GO_CLIMAX_GIRLS, Unit.TEAM_STELLA)),
    /**  */
    KIRIKO("幽谷霧子", "結名美月", List.of(Unit.LANTICA, Unit.TEAM_LUNA)),
    /**  */
    KOGANE("月岡恋鐘", "礒部花凜", List.of(Unit.LANTICA, Unit.TEAM_STELLA)),
    /**  */
    KOITO("福丸小糸", "田嶌紗蘭", List.of(Unit.NOCTCHILL, Unit.TEAM_LUNA)),
    /**  */
    LUCA("斑鳩ルカ", "川口莉奈", List.of(Unit.IKARUGA_LUCA)),
    /**  */
    MADOKA("樋口円香", "土屋李央", List.of(Unit.NOCTCHILL, Unit.TEAM_STELLA)),
    /**  */
    MAMIMI("田中摩美々", "菅沼千紗", List.of(Unit.LANTICA, Unit.TEAM_LUNA)),
    /**  */
    MANO("櫻木真乃", "関根瞳", List.of(Unit.ILLUMINATION_STARS, Unit.TEAM_STELLA)),
    /**  */
    MEGURU("八宮めぐる", "峯田茉優", List.of(Unit.ILLUMINATION_STARS, Unit.TEAM_SOL)),
    /**  */
    MEI("和泉愛依", "北原沙弥香", List.of(Unit.STRAYLIGHT, Unit.TEAM_LUNA)),
    /**  */
    MIKOTO("緋田美琴", "山根綺", List.of(Unit.SHHIS)),
    /**  */
    NATSUHA("有栖川夏葉", "涼本あきほ", List.of(Unit.HO_KA_GO_CLIMAX_GIRLS, Unit.TEAM_SOL)),
    /**  */
    NICHIKA("七草にちか", "紫月杏朱彩", List.of(Unit.SHHIS)),
    /**  */
    RINZE("杜野凛世", "丸岡和佳奈", List.of(Unit.HO_KA_GO_CLIMAX_GIRLS, Unit.TEAM_LUNA)),
    /**  */
    SAKUYA("白瀬咲耶", "八巻アンナ", List.of(Unit.LANTICA, Unit.TEAM_SOL)),
    /**  */
    TENKA("大崎甜花", "前川涼子", List.of(Unit.ALISTROEMERIA, Unit.TEAM_LUNA)),
    /**  */
    TORU("浅倉透", "和久井優", List.of(Unit.NOCTCHILL, Unit.TEAM_SOL)),
    /**  */
    YUIKA_1("三峰結華", "成海瑠奈", List.of(Unit.LANTICA, Unit.TEAM_LUNA)),
    YUIKA_2("三峰結華", "希水しお", List.of(Unit.LANTICA, Unit.TEAM_LUNA)),
    ;
    
    private final String characterName;
    
    private final String cvName;
    
    private final List<Unit> unitList;
    
    public static Character getFromCharacterName(String characterName) {
        return Arrays.stream(Character.values())
                     .filter(e -> StringUtils.equals(e.characterName, characterName))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                         "値: " + characterName + " のEnum定数は定義されていません")
                     );
    }
    
    public static List<Character> getFromUnit(Unit unit) {
        return Arrays.stream(Character.values())
                     .filter(e -> e.unitList.contains(unit))
                     .toList();
    }
}
