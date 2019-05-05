/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.utils;

import lombok.Getter;

import java.util.Arrays;

public enum  Enchantments_1_8 {
    PROTECTION(0),
    FIRE_PROTECTION(1),
    FEATHER_FALLING(2),
    BLAST_PROTECTION(3),
    PROJECTILE_PROTECTION(4),
    RESPIRATION(5),
    AQUA_AFFINITY(6),
    THORNS(7),
    DEPTH_STRIDER(8),
    FROST_WALKER(9),
    CURSE_OF_BINDING(10),
    SHARPNESS(16),
    SMITE(17),
    BANE_OF_ARTHROPODS(18),
    KNOCKBACK(19),
    FIRE_ASPECT(20),
    LOOTING(21),
    SWEEPING_EDGE(22),
    EFFICIENCY(32),
    SILK_TOUCH(33),
    UNBREAKING(34),
    FORTUNE(35),
    POWER(48),
    PUNCH(49),
    FLAME(50),
    INFINITY(51),
    LUCK_OF_THE_SEA(61),
    LURE(62),
    MENDING(70),
    CURSE_OF_VANISHING(71);

    @Getter private int id;

    Enchantments_1_8(int id) {
        this.id = id;
    }

    public static Enchantments_1_8 getFromId(int id) {
        return Arrays.stream(values()).filter(enchantments_1_8 -> enchantments_1_8.getId() == id).findFirst().get();
    }

}
