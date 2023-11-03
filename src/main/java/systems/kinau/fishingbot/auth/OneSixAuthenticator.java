package systems.kinau.fishingbot.auth;

import net.minecraft.OneSixParamStorage;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.UUIDUtils;

import java.util.Optional;

public class OneSixAuthenticator implements IAuthenticator {

    @Override
    public Optional<AuthData> authenticate() {
        FishingBot.getI18n().info("auth-using-onesix");
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if (oneSix != null) {
            String uuid = UUIDUtils.withDashes(oneSix.getUuid());
            return Optional.of(new AuthData(oneSix.getAccessToken(), uuid, oneSix.getUsername()));
        }
        return Optional.empty();
    }

}
