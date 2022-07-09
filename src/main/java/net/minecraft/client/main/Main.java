package net.minecraft.client.main;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.OneSixParamStorage;
import net.minecraft.client.util.Session.AccountType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.function.LongSupplier;

public class Main {
    public static LongSupplier nanoTimeSupplier = System::nanoTime;

    public static long getMeasuringTimeMs() {
        return getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }

    public static void main(String[] args) {
        System.out.println("FishingBot Minecraft Launcher Integration");

        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("disableMultiplayer");
        optionParser.accepts("disableChat");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        OptionSpec<String> optionSpec2 = optionParser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionSpec3 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionSpec4 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionSpec5 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionSpec6 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionSpec7 = optionParser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionSpec8 = optionParser.accepts("proxyPort").withRequiredArg().ofType(Integer.class).defaultsTo(8080);
        OptionSpec<String> optionSpec9 = optionParser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionSpec10 = optionParser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionSpec11 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + getMeasuringTimeMs() % 1000L);
        OptionSpec<String> optionSpec12 = optionParser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionSpec13 = optionParser.accepts("xuid").withOptionalArg().defaultsTo("");
        OptionSpec<String> optionSpec14 = optionParser.accepts("clientId").withOptionalArg().defaultsTo("");
        OptionSpec<String> optionSpec15 = optionParser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionSpec16 = optionParser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionSpec17 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionSpec18 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<Integer> optionSpec19 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        OptionSpec<Integer> optionSpec20 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> optionSpec21 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionSpec22 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionSpec23 = optionParser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionSpec24 = optionParser.accepts("userType").withRequiredArg().defaultsTo(AccountType.LEGACY.getName());
        OptionSpec<String> optionSpec25 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release");
        OptionSpec<String> optionSpec26 = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List<String> list = optionSet.valuesOf(optionSpec26);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        Integer optionValue3 = getOption(optionSet, optionSpec3);
        Integer optionValue8 = getOption(optionSet, optionSpec8);
        Integer optionValue17 = getOption(optionSet, optionSpec17);
        Integer optionValue18 = getOption(optionSet, optionSpec18);
        Integer optionValue19 = getOption(optionSet, optionSpec19);
        Integer optionValue20 = getOption(optionSet, optionSpec20);

        OneSixParamStorage oneSix = OneSixParamStorage.makeInstance();
        oneSix.setServer(getOption(optionSet, optionSpec2));
        oneSix.setPort(optionValue3 == null ? 25565 : optionValue3);
        oneSix.setGameDir(getOption(optionSet, optionSpec4));
        oneSix.setAssetsDir(getOption(optionSet, optionSpec5));
        oneSix.setResourcePackDir(getOption(optionSet, optionSpec6));
        oneSix.setProxyHost(getOption(optionSet, optionSpec7));
        oneSix.setProxyPort(optionValue8 == null ? 8080 : optionValue8);
        oneSix.setProxyUser(getOption(optionSet, optionSpec9));
        oneSix.setProxyPass(getOption(optionSet, optionSpec10));
        oneSix.setUsername(getOption(optionSet, optionSpec11));
        oneSix.setUuid(getOption(optionSet, optionSpec12));
        oneSix.setXuid(getOption(optionSet, optionSpec13));
        oneSix.setClientId(getOption(optionSet, optionSpec14));
        oneSix.setAccessToken(getOption(optionSet, optionSpec15));
        oneSix.setVersion(getOption(optionSet, optionSpec16));
        oneSix.setWidth(optionValue17 == null ? 854 : optionValue17);
        oneSix.setHeight(optionValue18 == null ? 480 : optionValue18);
        oneSix.setFullscreenWidth(optionValue19 == null ? 0 : optionValue19);
        oneSix.setFullscreenHeight(optionValue20 == null ? 0 : optionValue20);
        oneSix.setUserProperties(getOption(optionSet, optionSpec21));
        oneSix.setProfileProperties(getOption(optionSet, optionSpec22));
        oneSix.setAssetIndex(getOption(optionSet, optionSpec23));
        oneSix.setUserType(getOption(optionSet, optionSpec24));
        oneSix.setVersionType(getOption(optionSet, optionSpec25));

        systems.kinau.fishingbot.Main.main(new String[0]);
    }

    @Nullable
    private static <T> T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return optionSet.valueOf(optionSpec);
        } catch (Throwable var5) {
            if (optionSpec instanceof ArgumentAcceptingOptionSpec) {
                ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec<T>) optionSpec;
                List<T> list = argumentAcceptingOptionSpec.defaultValues();
                if (!list.isEmpty()) {
                    return list.get(0);
                }
            }

            throw var5;
        }
    }
}
