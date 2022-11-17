package systems.kinau.fishingbot.auth;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.msa.*;
import systems.kinau.fishingbot.gui.Dialogs;
import systems.kinau.fishingbot.utils.reflect.MethodAccessor;
import systems.kinau.fishingbot.utils.reflect.Reflect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MicrosoftAuthenticator implements IAuthenticator {

    private final static MethodAccessor LOGIN_RESPONSE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getLoginResponseFromToken", String.class);
    private final static MethodAccessor GET_PROFILE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getProfile");

    public static final String CLIENT_ID = "fef9faea-d962-4476-9ce7-4960c8baa946";
    private final static Gson GSON = new Gson();

    @Override
    public Optional<AuthData> authenticate() {
        FishingBot.getI18n().info("auth-using-microsoft");
        String refreshToken = readRefreshToken();
        CompletableFuture<Alert> authDialog = null;

        try {
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
                    authDialog = Dialogs.showAuthorizationRequest(callback.getUserCode(), callback.getVerificationUrl());
                }

                try {
                    refreshToken = RefreshTokenCallback.await(callback, CLIENT_ID);
                } catch (ObtainTokenException ex) {
                    ex.printStackTrace();
                    closeDialog(authDialog);
                    if (!FishingBot.getInstance().getCurrentBot().isNoGui()) {
                        Dialogs.showAuthFailed(ex.getReason());
                    }
                    FishingBot.getInstance().getCurrentBot().setPreventStartup(true);
                    return Optional.empty();
                }
            } else {
                FishingBot.getI18n().info("auth-found-refresh-token", FishingBot.getInstance().getRefreshTokenFile().getAbsolutePath());
            }

            if (refreshToken == null) {
                FishingBot.getI18n().severe("auth-could-not-get-refresh-token");
                closeDialog(authDialog);
                return Optional.empty();
            }

            setDialogProgress(authDialog, 0.1);

            try {
                Files.write(refreshToken, FishingBot.getInstance().getRefreshTokenFile(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AuthenticationService authService = new MsaAuthenticationService(CLIENT_ID, refreshToken);
            setDialogProgress(authDialog, 0.2);
            AccessTokenCallback callback = AccessTokenGenerator.createAccessToken(refreshToken, CLIENT_ID);

            if (callback == null) {
                FishingBot.getI18n().severe("auth-could-not-get-access-token");
                closeDialog(authDialog);
                return Optional.empty();
            }
            setDialogProgress(authDialog, 0.5);
            JsonObject object = GSON.toJsonTree(LOGIN_RESPONSE_ACCESSOR.invoke(authService, "d=" + callback.getAccessToken())).getAsJsonObject();
            authService.setAccessToken(object.get("access_token").getAsString());
            setDialogProgress(authDialog, 0.9);
            MicrosoftAuthenticator.GET_PROFILE_ACCESSOR.invoke(authService);
            closeDialog(authDialog);
            return Optional.of(new AuthData(authService.getAccessToken(), authService.getSelectedProfile().getIdAsString(), authService.getSelectedProfile().getName()));
        } catch (Throwable e) {
            e.printStackTrace();
            closeDialog(authDialog);
            return Optional.empty();
        }
    }

    private void setDialogProgress(CompletableFuture<Alert> authDialog, double progress) {
        modifyDialog(authDialog, alert -> {
            Node flowPane = alert.getDialogPane().contentProperty().get();
            if (flowPane instanceof FlowPane) {
                ObservableList<Node> children = ((FlowPane) flowPane).getChildren();
                if (children.size() >= 3) {
                    Node progressBar = children.get(2);
                    if (progressBar instanceof ProgressBar) {
                        ((ProgressBar) progressBar).setProgress(progress);
                    }
                }
            }
        });
    }

    private void closeDialog(CompletableFuture<Alert> authDialog) {
        modifyDialog(authDialog, alert -> {
            alert.setResult(ButtonType.OK);
            alert.close();
        });
    }

    private void modifyDialog(CompletableFuture<Alert> authDialog, Consumer<Alert> consumer) {
        if (authDialog != null) {
            Alert alert = authDialog.join();
            Platform.runLater(() -> consumer.accept(alert));
        }
    }

    private String readRefreshToken() {
        File file = FishingBot.getInstance().getRefreshTokenFile();
        
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
