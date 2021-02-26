package systems.kinau.fishingbot.auth;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.StringUtils;

import java.util.Optional;
import java.util.UUID;

public class MicrosoftAuthenticator implements IAuthenticator {

    @Override
    public Optional<AuthData> authenticate(String loginName, String password) {
        FishingBot.getI18n().info("auth-using-password", StringUtils.maskUsername(loginName), AuthService.MICROSOFT);
        MsaAuthenticationService authService = new MsaAuthenticationService(new UUID(2327498789580459489L, -5076311871023784568L).toString());
        authService.setUsername(loginName);
        authService.setPassword(password);
        try {
            authService.login();
            FishingBot.getI18n().info("auth-successful");
            return Optional.of(new AuthData(authService.getAccessToken(), authService.getClientToken(), authService.getSelectedProfile().getIdAsString(), authService.getUsername()));
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
