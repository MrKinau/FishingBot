package net.minecraft;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class OneSixParamStorage {
    private static OneSixParamStorage instance;

    @Getter @Setter String server;
    @Getter @Setter int port;
    @Getter @Setter File gameDir;
    @Getter @Setter File assetsDir;
    @Getter @Setter File resourcePackDir;
    @Getter @Setter String proxyHost;
    @Getter @Setter int proxyPort;
    @Getter @Setter String proxyUser;
    @Getter @Setter String proxyPass;
    @Getter @Setter String username;
    @Getter @Setter String uuid;
    @Getter @Setter String xuid;
    @Getter @Setter String clientId;
    @Getter @Setter String accessToken;
    @Getter @Setter String version;
    @Getter @Setter int width;
    @Getter @Setter int height;
    @Getter @Setter int fullscreenWidth;
    @Getter @Setter int fullscreenHeight;
    @Getter @Setter String userProperties;
    @Getter @Setter String profileProperties;
    @Getter @Setter String assetIndex;
    @Getter @Setter String userType;
    @Getter @Setter String versionType;

    private OneSixParamStorage() {
    }

    public static OneSixParamStorage makeInstance() {
        instance = new OneSixParamStorage();
        return instance;
    }

    public static OneSixParamStorage getInstance() {
        return instance;
    }
}
