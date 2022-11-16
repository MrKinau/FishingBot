package systems.kinau.fishingbot.auth;

import net.minecraft.OneSixParamStorage;
import systems.kinau.fishingbot.FishingBot;

import java.util.Optional;

public class Authenticator {

    public Optional<AuthData> authenticate() {
        // Automatically change auth service if OneSixLauncher is available
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();

        if (oneSix != null) {
            FishingBot.getI18n().info("auth-change-onesix");
            return new OneSixAuthenticator().authenticate();
        }

        IAuthenticator authenticator = new MicrosoftAuthenticator();

        return authenticator.authenticate();
    }

}
