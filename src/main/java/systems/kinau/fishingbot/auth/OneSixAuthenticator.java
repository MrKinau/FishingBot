package systems.kinau.fishingbot.auth;

import net.minecraft.OneSixParamStorage;
import systems.kinau.fishingbot.FishingBot;

import java.util.Optional;

public class OneSixAuthenticator implements IAuthenticator {

    @Override
    public Optional<AuthData> authenticate() {
        FishingBot.getI18n().info("auth-using-onesix");
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if (oneSix != null) {
            String uuid = oneSix.getUuid().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            return Optional.of(new AuthData(oneSix.getAccessToken(), uuid, oneSix.getUsername()));
        }
        return Optional.empty();
    }

}
