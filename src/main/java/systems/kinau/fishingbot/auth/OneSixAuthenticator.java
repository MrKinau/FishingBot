package systems.kinau.fishingbot.auth;

import net.minecraft.OneSixParamStorage;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.StringUtils;

import java.io.File;
import java.util.Optional;

public class OneSixAuthenticator implements IAuthenticator {
    @Override
    public Optional<AuthData> authenticate(String loginName, String password) {
        FishingBot.getI18n().info("auth-using-onesix", AuthService.ONESIX);
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if (oneSix != null) {
            System.out.printf("Username: " + oneSix.getUsername());
            System.out.printf("Access Token: " + oneSix.getAccessToken());
            System.out.printf("Profile ID: " + oneSix.getUuid());
            return Optional.of(new AuthData(oneSix.getAccessToken(), "", oneSix.getUuid(), oneSix.getUsername()));
        }
        return Optional.empty();
    }
}
