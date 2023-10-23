/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/4
 */

package systems.kinau.fishingbot.modules.fishing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;

@AllArgsConstructor
@Getter
public enum AnnounceType implements DisplayNameProvider {
    ALL("Everything"),
    ALL_BUT_FISH("Everything Except Fish"),
    ONLY_ENCHANTED("Only Enchanted Items"),
    ONLY_BOOKS("Only Enchanted Books"),
    NONE("Nothing");

    private final String displayName;
}
