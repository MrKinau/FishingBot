package systems.kinau.fishingbot.auth;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.msa.*;
import systems.kinau.fishingbot.gui.Dialogs;
import systems.kinau.fishingbot.utils.reflect.MethodAccessor;
import systems.kinau.fishingbot.utils.reflect.Reflect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class MicrosoftAuthenticator implements IAuthenticator {

    private final static MethodAccessor LOGIN_RESPONSE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getLoginResponseFromToken", String.class);
    private final static MethodAccessor GET_PROFILE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getProfile");

    public static final String CLIENT_ID = "fef9faea-d962-4476-9ce7-4960c8baa946";
    private final static Gson GSON = new Gson();

    @Override
    public Optional<AuthData> authenticate() {
        FishingBot.getI18n().info("auth-using-microsoft");
        String refreshToken = readRefreshToken();

        if (refreshToken == null) {
            DeviceTokenCallback callback = DeviceTokenGenerator.createDeviceToken(CLIENT_ID);

            if (callback == null) {
                FishingBot.getI18n().severe("auth-could-not-get-refresh-token");
                return Optional.empty();
            }

            FishingBot.getLog().warning(" ");
            FishingBot.getLog().warning(" ");
            for (String line : FishingBot.getI18n().t("auth-create-refresh-token", callback.getUserCode(), callback.getVerificationUrl()).split("\n")) {
                FishingBot.getLog().warning(line);
            }
            FishingBot.getLog().warning(" ");

            if (!FishingBot.getInstance().getCurrentBot().isNoGui()) {
                Dialogs.showAuthorizationRequest(callback.getUserCode(), callback.getVerificationUrl());
            }

            refreshToken = RefreshTokenCallback.await(callback, CLIENT_ID);
        }

        if (refreshToken == null) {
            FishingBot.getI18n().severe("auth-could-not-get-refresh-token");
            return Optional.empty();
        }

        try {
            Files.write(refreshToken, new File("refreshToken"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AuthenticationService authService = new MsaAuthenticationService(CLIENT_ID, refreshToken);
        AccessTokenCallback callback = AccessTokenGenerator.createAccessToken(refreshToken, CLIENT_ID);

        if (callback == null) {
            FishingBot.getI18n().severe("auth-could-not-get-access-token");
            return Optional.empty();
        }

        JsonObject object = GSON.toJsonTree(LOGIN_RESPONSE_ACCESSOR.invoke(authService, "d=" + callback.getAccessToken())).getAsJsonObject();
        authService.setAccessToken(object.get("access_token").getAsString());
        MicrosoftAuthenticator.GET_PROFILE_ACCESSOR.invoke(authService);

        return Optional.of(new AuthData(authService.getAccessToken(), authService.getSelectedProfile().getIdAsString(), authService.getSelectedProfile().getName()));
    }

    private String readRefreshToken() {
        File file = new File("refreshToken");
        
        if (!file.exists()) {
            return null;
        }

        try {
            return Files.readFirstLine(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
