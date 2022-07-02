package systems.kinau.fishingbot.auth;

import net.minecraft.OneSixParamStorage;
import systems.kinau.fishingbot.FishingBot;

import java.util.Optional;

public class OneSixAuthenticator implements IAuthenticator {
    @Override
    public Optional<AuthData> authenticate(String loginName, String password) {
        FishingBot.getI18n().info("auth-using-onesix", AuthService.ONESIX);
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if (oneSix != null) {
            return Optional.of(new AuthData(oneSix.getAccessToken(), "", oneSix.getUuid(), oneSix.getUsername()));
        }
        return Optional.empty();
    }
}
