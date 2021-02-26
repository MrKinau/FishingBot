/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ProtocolConstants {

    public static final int AUTOMATIC = -1;
    public static final int MINECRAFT_1_8 = 47;
    public static final int MINECRAFT_1_9 = 107;
    public static final int MINECRAFT_1_9_1 = 108;
    public static final int MINECRAFT_1_9_2 = 109;
    public static final int MINECRAFT_1_9_4 = 110;
    public static final int MINECRAFT_1_10 = 210;
    public static final int MINECRAFT_1_11 = 315;
    public static final int MINECRAFT_1_11_1 = 316;
    public static final int MINECRAFT_1_12 = 335;
    public static final int MINECRAFT_1_12_1 = 338;
    public static final int MINECRAFT_1_12_2 = 340;
    public static final int MINECRAFT_1_13 = 393;
    public static final int MINECRAFT_1_13_1 = 401;
    public static final int MINECRAFT_1_13_2 = 404;
    public static final int MINECRAFT_1_14 = 477;
    public static final int MINECRAFT_1_14_1 = 480;
    public static final int MINECRAFT_1_14_2 = 485;
    public static final int MINECRAFT_1_14_3 = 490;
    public static final int MINECRAFT_1_14_4 = 498;
    public static final int MINECRAFT_1_15 = 573;
    public static final int MINECRAFT_1_15_1 = 575;
    public static final int MINECRAFT_1_15_2 = 578;
    public static final int MINECRAFT_1_16 = 735;
    public static final int MINECRAFT_1_16_1 = 736;
    public static final int MINECRAFT_1_16_2 = 751;
    public static final int MINECRAFT_1_16_3 = 753;
    public static final int MINECRAFT_1_16_4 = 754;

    public static final List<Integer> SUPPORTED_VERSION_IDS = Arrays.asList(
            ProtocolConstants.AUTOMATIC,
            ProtocolConstants.MINECRAFT_1_8,
            ProtocolConstants.MINECRAFT_1_9,
            ProtocolConstants.MINECRAFT_1_9_1,
            ProtocolConstants.MINECRAFT_1_9_2,
            ProtocolConstants.MINECRAFT_1_9_4,
            ProtocolConstants.MINECRAFT_1_10,
            ProtocolConstants.MINECRAFT_1_11,
            ProtocolConstants.MINECRAFT_1_11_1,
            ProtocolConstants.MINECRAFT_1_12,
            ProtocolConstants.MINECRAFT_1_12_1,
            ProtocolConstants.MINECRAFT_1_12_2,
            ProtocolConstants.MINECRAFT_1_13,
            ProtocolConstants.MINECRAFT_1_13_1,
            ProtocolConstants.MINECRAFT_1_13_2,
            ProtocolConstants.MINECRAFT_1_14,
            ProtocolConstants.MINECRAFT_1_14_1,
            ProtocolConstants.MINECRAFT_1_14_2,
            ProtocolConstants.MINECRAFT_1_14_3,
            ProtocolConstants.MINECRAFT_1_14_4,
            ProtocolConstants.MINECRAFT_1_15,
            ProtocolConstants.MINECRAFT_1_15_1,
            ProtocolConstants.MINECRAFT_1_15_2,
            ProtocolConstants.MINECRAFT_1_16,
            ProtocolConstants.MINECRAFT_1_16_1,
            ProtocolConstants.MINECRAFT_1_16_2,
            ProtocolConstants.MINECRAFT_1_16_3,
            ProtocolConstants.MINECRAFT_1_16_4

    );

    public static String getVersionString(int protocolId) {
        switch (protocolId) {
            case AUTOMATIC: return "AUTOMATIC";
            case MINECRAFT_1_8: return "1.8";
            case MINECRAFT_1_9: return "1.9";
            case MINECRAFT_1_9_1: return "1.9.1";
            case MINECRAFT_1_9_2: return "1.9.2";
            case MINECRAFT_1_9_4: return "1.9.4";
            case MINECRAFT_1_10: return "1.10";
            case MINECRAFT_1_11: return "1.11";
            case MINECRAFT_1_11_1: return "1.11.1";
            case MINECRAFT_1_12: return "1.12";
            case MINECRAFT_1_12_1: return "1.12.1";
            case MINECRAFT_1_12_2: return "1.12.2";
            case MINECRAFT_1_13: return "1.13";
            case MINECRAFT_1_13_1: return "1.13.1";
            case MINECRAFT_1_13_2: return "1.13.2";
            case MINECRAFT_1_14: return "1.14";
            case MINECRAFT_1_14_1: return "1.14.1";
            case MINECRAFT_1_14_2: return "1.14.2";
            case MINECRAFT_1_14_3: return "1.14.3";
            case MINECRAFT_1_14_4: return "1.14.4";
            case MINECRAFT_1_15: return "1.15";
            case MINECRAFT_1_15_1: return "1.15.1";
            case MINECRAFT_1_15_2: return "1.15.2";
            case MINECRAFT_1_16: return "1.16";
            case MINECRAFT_1_16_1: return "1.16.1";
            case MINECRAFT_1_16_2: return "1.16.2";
            case MINECRAFT_1_16_3: return "1.16.3";
            case MINECRAFT_1_16_4: return "1.16.4 / 1.16.5";
            default: return "Unknown version";
        }
    }

    public static int getProtocolId(String versionString) {
        switch (versionString) {
            case "AUTOMATIC": return AUTOMATIC;
            case "1.9": return MINECRAFT_1_9;
            case "1.9.1": return MINECRAFT_1_9_1;
            case "1.9.2": return MINECRAFT_1_9_2;
            case "1.9.4": return MINECRAFT_1_9_4;
            case "1.10": return MINECRAFT_1_10;
            case "1.11": return MINECRAFT_1_11;
            case "1.11.1": return MINECRAFT_1_11_1;
            case "1.12": return MINECRAFT_1_12;
            case "1.12.1": return MINECRAFT_1_12_1;
            case "1.12.2": return MINECRAFT_1_12_2;
            case "1.13": return MINECRAFT_1_13;
            case "1.13.1": return MINECRAFT_1_13_1;
            case "1.13.2": return MINECRAFT_1_13_2;
            case "1.14": return MINECRAFT_1_14;
            case "1.14.1": return MINECRAFT_1_14_1;
            case "1.14.2": return MINECRAFT_1_14_2;
            case "1.14.3": return MINECRAFT_1_14_3;
            case "1.14.4": return MINECRAFT_1_14_4;
            case "1.15": return MINECRAFT_1_15;
            case "1.15.1": return MINECRAFT_1_15_1;
            case "1.15.2": return MINECRAFT_1_15_2;
            case "1.16": return MINECRAFT_1_16;
            case "1.16.1": return MINECRAFT_1_16_1;
            case "1.16.2": return MINECRAFT_1_16_2;
            case "1.16.3": return MINECRAFT_1_16_3;
            case "1.16.4 / 1.16.5":
            case "1.16.5":
            case "1.16.4": return MINECRAFT_1_16_4;
            default: return MINECRAFT_1_8;
        }
    }

    public static int getLatest() {
        return SUPPORTED_VERSION_IDS.stream().max(Comparator.naturalOrder()).orElse(0);
    }
}
