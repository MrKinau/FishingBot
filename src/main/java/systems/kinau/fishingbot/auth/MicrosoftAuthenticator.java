package systems.kinau.fishingbot.auth;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.StringUtils;

import java.util.Optional;

public class MicrosoftAuthenticator implements IAuthenticator {

    @Override
    public Optional<AuthData> authenticate(String loginName, String password) {
        FishingBot.getI18n().info("auth-using-password", StringUtils.maskUsername(loginName), AuthService.MICROSOFT);
        MsaAuthenticationService authService = new MsaAuthenticationService("fef9faea-d962-4476-9ce7-4960c8baa946");
        authService.setUsername(loginName);
        authService.setPassword(password);
        try {
            authService.login();
            FishingBot.getI18n().info("auth-successful");
            GameProfile profile = authService.getSelectedProfile();
            return Optional.of(new AuthData(authService.getAccessToken(), authService.getClientToken(), profile == null ? null : profile.getIdAsString().replace("-", ""), authService.getUsername()));
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
