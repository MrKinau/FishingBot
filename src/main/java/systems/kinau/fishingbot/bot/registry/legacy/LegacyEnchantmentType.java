/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.bot.registry.legacy;

import lombok.Getter;

@Getter
public enum LegacyEnchantmentType {

    PROTECTION(0, "protection"),
    FIRE_PROTECTION(1, "fire_protection"),
    FEATHER_FALLING(2, "feather_falling"),
    BLAST_PROTECTION(3, "blast_protection"),
    PROJECTILE_PROTECTION(4, "projectile_protection"),
    RESPIRATION(5, "respiration"),
    AQUA_AFFINITY(6, "aqua_affinity"),
    THORNS(7, "thorns"),
    DEPTH_STRIDER(8, "depth_strider"),
    FROST_WALKER(9, "frost_walker"),
    CURSE_OF_BINDING(10, "binding_curse"),
    SOUL_SPEED(-1, "soul_speed"),
    SHARPNESS(16, "sharpness"),
    SMITE(17, "smite"),
    BANE_OF_ARTHROPODS(18, "bane_of_arthropods"),
    KNOCKBACK(19, "knockback"),
    FIRE_ASPECT(20, "fire_aspect"),
    LOOTING(21, "looting"),
    SWEEPING_EDGE(22, "sweeping"),
    EFFICIENCY(32, "efficiency"),
    SILK_TOUCH(33, "silk_touch"),
    UNBREAKING(34, "unbreaking"),
    FORTUNE(35, "fortune"),
    POWER(48, "power"),
    PUNCH(49, "punch"),
    FLAME(50, "flame"),
    INFINITY(51, "infinity"),
    LUCK_OF_THE_SEA(61, "luck_of_the_sea"),
    LURE(62, "lure"),
    LOYALTY(-1, "loyalty"),
    IMPALING(-1, "impaling"),
    RIPTIDE(-1, "riptide"),
    CHANNELING(-1, "channeling"),
    MULTISHOT(-1, "multishot"),
    QUICK_CHARGE(-1, "quick_charge"),
    PIERCING(-1, "piercing"),
    MENDING(70, "mending"),
    CURSE_OF_VANISHING(71, "vanishing_curse"),
    SWIFT_SNEAK(-1, "swift_sneak")
    ;

    private final String name;
    private final int legacyId;

    LegacyEnchantmentType(int legacyId, String name) {
        this.legacyId = legacyId;
        this.name = name;
    }
}
