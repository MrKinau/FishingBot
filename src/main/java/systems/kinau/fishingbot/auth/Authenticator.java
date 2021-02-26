package systems.kinau.fishingbot.auth;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import systems.kinau.fishingbot.FishingBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Authenticator {

    private final File accountFile;

    public Authenticator(File accountFile) {
        this.accountFile = accountFile;
    }

    public Optional<AuthData> authenticate(AuthService authService) {
        MojangAuthenticator mjAuthenticator = new MojangAuthenticator(accountFile);
        String userName = FishingBot.getInstance().getCurrentBot().getConfig().getUserName();
        String password = FishingBot.getInstance().getCurrentBot().getConfig().getPassword();
        if (accountFile.exists()) {
            try {
                List<String> content = Files.readAllLines(Paths.get(accountFile.toURI()));
                String json = String.join("", content);
                JSONObject rootObj = (JSONObject) new JSONParser().parse(json);
                String accessToken = (String) rootObj.get("accessToken");
                String clientToken = (String) rootObj.get("clientToken");
                String loginName = (String) rootObj.get("loginName");
                String accountName = (String) rootObj.get("accountName");
                String profileId = (String) rootObj.get("profileId");
                if (loginName.equals(FishingBot.getInstance().getCurrentBot().getConfig().getUserName())) {
                    Optional<AuthData> authData = mjAuthenticator.authenticateWithTokens(accessToken, clientToken, accountName, profileId);
                    if (authData.isPresent())
                        return authData;
                }
            } catch (Exception e) {
                FishingBot.getI18n().warning("auth-file-could-not-be-read", accountFile.getName(), e.getMessage());
            }
        }
        IAuthenticator authenticator = mjAuthenticator;
        if (authService == AuthService.MICROSOFT)
            authenticator = new MicrosoftAuthenticator();

        Optional<AuthData> authData = authenticator.authenticate(userName, password);
        authData.ifPresent(data -> writeAccountFile(data.getAccessToken(), data.getClientToken(),
                userName, data.getUsername(), data.getProfile()));
        return authData;
    }

    private void writeAccountFile(String accessToken, String clientToken, String loginName, String accountName, String profileId) {
        org.json.JSONObject rootObj = new org.json.JSONObject();
        rootObj.put("accessToken", accessToken);
        rootObj.put("clientToken", clientToken);
        rootObj.put("loginName", loginName);
        rootObj.put("accountName", accountName);
        rootObj.put("profileId", profileId);
        String output = rootObj.toString(4);
        try {
            Files.write(Paths.get(accountFile.toURI()), Arrays.asList(output.split("\n")));
        } catch (IOException e) {
            FishingBot.getI18n().warning("auth-file-could-not-be-saved", accountFile.getName(), e.getMessage());
        }
    }

}
