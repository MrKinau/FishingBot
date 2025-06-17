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
    public static final int MC_1_8 = 47;
    public static final int MC_1_9 = 107;
    public static final int MC_1_9_1 = 108;
    public static final int MC_1_9_2 = 109;
    public static final int MC_1_9_4 = 110;
    public static final int MC_1_10 = 210;
    public static final int MC_1_11 = 315;
    public static final int MC_1_11_1 = 316;
    public static final int MC_1_12 = 335;
    public static final int MC_1_12_1 = 338;
    public static final int MC_1_12_2 = 340;
    public static final int MC_1_13 = 393;
    public static final int MC_1_13_1 = 401;
    public static final int MC_1_13_2 = 404;
    public static final int MC_1_14 = 477;
    public static final int MC_1_14_1 = 480;
    public static final int MC_1_14_2 = 485;
    public static final int MC_1_14_3 = 490;
    public static final int MC_1_14_4 = 498;
    public static final int MC_1_15 = 573;
    public static final int MC_1_15_1 = 575;
    public static final int MC_1_15_2 = 578;
    public static final int MC_1_16 = 735;
    public static final int MC_1_16_1 = 736;
    public static final int MC_1_16_2 = 751;
    public static final int MC_1_16_3 = 753;
    public static final int MC_1_16_4 = 754;
    public static final int MC_1_17 = 755;
    public static final int MC_1_17_1 = 756;
    public static final int MC_1_18 = 757;
    public static final int MC_1_18_2 = 758;
    public static final int MC_1_19 = 759;
    public static final int MC_1_19_1 = 760;
    public static final int MC_1_19_3 = 761;
    public static final int MC_1_19_4 = 762;
    public static final int MC_1_20 = 763;
    public static final int MC_1_20_2 = 764;
    public static final int MC_1_20_3 = 765;
    public static final int MC_1_20_5 = 766;
    public static final int MC_1_21 = 767;
    public static final int MC_1_21_2 = 768;
    public static final int MC_1_21_4 = 769;
    public static final int MC_1_21_5 = 770;
    public static final int MC_1_21_6 = 771;

    public static final List<Integer> SUPPORTED_VERSION_IDS = Arrays.asList(
            ProtocolConstants.AUTOMATIC,
            ProtocolConstants.MC_1_8,
            ProtocolConstants.MC_1_9,
            ProtocolConstants.MC_1_9_1,
            ProtocolConstants.MC_1_9_2,
            ProtocolConstants.MC_1_9_4,
            ProtocolConstants.MC_1_10,
            ProtocolConstants.MC_1_11,
            ProtocolConstants.MC_1_11_1,
            ProtocolConstants.MC_1_12,
            ProtocolConstants.MC_1_12_1,
            ProtocolConstants.MC_1_12_2,
            ProtocolConstants.MC_1_13,
            ProtocolConstants.MC_1_13_1,
            ProtocolConstants.MC_1_13_2,
            ProtocolConstants.MC_1_14,
            ProtocolConstants.MC_1_14_1,
            ProtocolConstants.MC_1_14_2,
            ProtocolConstants.MC_1_14_3,
            ProtocolConstants.MC_1_14_4,
            ProtocolConstants.MC_1_15,
            ProtocolConstants.MC_1_15_1,
            ProtocolConstants.MC_1_15_2,
            ProtocolConstants.MC_1_16,
            ProtocolConstants.MC_1_16_1,
            ProtocolConstants.MC_1_16_2,
            ProtocolConstants.MC_1_16_3,
            ProtocolConstants.MC_1_16_4,
            ProtocolConstants.MC_1_17,
            ProtocolConstants.MC_1_17_1,
            ProtocolConstants.MC_1_18,
            ProtocolConstants.MC_1_18_2,
            ProtocolConstants.MC_1_19,
            ProtocolConstants.MC_1_19_1,
            ProtocolConstants.MC_1_19_3,
            ProtocolConstants.MC_1_19_4,
            ProtocolConstants.MC_1_20,
            ProtocolConstants.MC_1_20_2,
            ProtocolConstants.MC_1_20_3,
            ProtocolConstants.MC_1_20_5,
            ProtocolConstants.MC_1_21,
            ProtocolConstants.MC_1_21_2,
            ProtocolConstants.MC_1_21_4,
            ProtocolConstants.MC_1_21_5,
            ProtocolConstants.MC_1_21_6
    );

    public static String getVersionString(int protocolId) {
        switch (protocolId) {
            case AUTOMATIC: return "AUTOMATIC";
            case MC_1_8: return "1.8";
            case MC_1_9: return "1.9";
            case MC_1_9_1: return "1.9.1";
            case MC_1_9_2: return "1.9.2";
            case MC_1_9_4: return "1.9.4";
            case MC_1_10: return "1.10";
            case MC_1_11: return "1.11";
            case MC_1_11_1: return "1.11.1";
            case MC_1_12: return "1.12";
            case MC_1_12_1: return "1.12.1";
            case MC_1_12_2: return "1.12.2";
            case MC_1_13: return "1.13";
            case MC_1_13_1: return "1.13.1";
            case MC_1_13_2: return "1.13.2";
            case MC_1_14: return "1.14";
            case MC_1_14_1: return "1.14.1";
            case MC_1_14_2: return "1.14.2";
            case MC_1_14_3: return "1.14.3";
            case MC_1_14_4: return "1.14.4";
            case MC_1_15: return "1.15";
            case MC_1_15_1: return "1.15.1";
            case MC_1_15_2: return "1.15.2";
            case MC_1_16: return "1.16";
            case MC_1_16_1: return "1.16.1";
            case MC_1_16_2: return "1.16.2";
            case MC_1_16_3: return "1.16.3";
            case MC_1_16_4: return "1.16.4 / 1.16.5";
            case MC_1_17: return "1.17";
            case MC_1_17_1: return "1.17.1";
            case MC_1_18: return "1.18 / 1.18.1";
            case MC_1_18_2: return "1.18.2";
            case MC_1_19: return "1.19";
            case MC_1_19_1: return "1.19.1 / 1.19.2";
            case MC_1_19_3: return "1.19.3";
            case MC_1_19_4: return "1.19.4";
            case MC_1_20: return "1.20 / 1.20.1";
            case MC_1_20_2: return "1.20.2";
            case MC_1_20_3: return "1.20.3 / 1.20.4";
            case MC_1_20_5: return "1.20.5 / 1.20.6";
            case MC_1_21: return "1.21 / 1.21.1";
            case MC_1_21_2: return "1.21.2 / 1.21.3";
            case MC_1_21_4: return "1.21.4";
            case MC_1_21_5: return "1.21.5";
            case MC_1_21_6: return "1.21.6";
            default: return "Unknown version";
        }
    }

    public static String getExactVersionString(int protocolId) {
        switch (protocolId) {
            case MC_1_16_4: return "1.16.5";
            case MC_1_18: return "1.18.1";
            case MC_1_19_1: return "1.19.2";
            case MC_1_20: return "1.20.1";
            case MC_1_20_3: return "1.20.4";
            case MC_1_20_5: return "1.20.6";
            case MC_1_21_2: return "1.21.3";
            default: return getVersionString(protocolId);
        }
    }

    public static int getProtocolId(String versionString) {
        switch (versionString) {
            case "AUTOMATIC": return AUTOMATIC;
            case "1.9": return MC_1_9;
            case "1.9.1": return MC_1_9_1;
            case "1.9.2": return MC_1_9_2;
            case "1.9.4": return MC_1_9_4;
            case "1.10": return MC_1_10;
            case "1.11": return MC_1_11;
            case "1.11.1": return MC_1_11_1;
            case "1.12": return MC_1_12;
            case "1.12.1": return MC_1_12_1;
            case "1.12.2": return MC_1_12_2;
            case "1.13": return MC_1_13;
            case "1.13.1": return MC_1_13_1;
            case "1.13.2": return MC_1_13_2;
            case "1.14": return MC_1_14;
            case "1.14.1": return MC_1_14_1;
            case "1.14.2": return MC_1_14_2;
            case "1.14.3": return MC_1_14_3;
            case "1.14.4": return MC_1_14_4;
            case "1.15": return MC_1_15;
            case "1.15.1": return MC_1_15_1;
            case "1.15.2": return MC_1_15_2;
            case "1.16": return MC_1_16;
            case "1.16.1": return MC_1_16_1;
            case "1.16.2": return MC_1_16_2;
            case "1.16.3": return MC_1_16_3;
            case "1.16.4 / 1.16.5":
            case "1.16.5":
            case "1.16.4": return MC_1_16_4;
            case "1.17": return MC_1_17;
            case "1.17.1": return MC_1_17_1;
            case "1.18":
            case "1.18.1":
            case "1.18 / 1.18.1": return MC_1_18;
            case "1.18.2": return MC_1_18_2;
            case "1.19": return MC_1_19;
            case "1.19.1":
            case "1.19.2":
            case "1.19.1 / 1.19.2": return MC_1_19_1;
            case "1.19.3": return MC_1_19_3;
            case "1.19.4": return MC_1_19_4;
            case "1.20":
            case "1.20.1":
            case "1.20 / 1.20.1": return MC_1_20;
            case "1.20.2": return MC_1_20_2;
            case "1.20.3":
            case "1.20.4":
            case "1.20.3 / 1.20.4": return MC_1_20_3;
            case "1.20.5":
            case "1.20.6":
            case "1.20.5 / 1.20.6": return MC_1_20_5;
            case "1.21":
            case "1.21.1":
            case "1.21 / 1.21.1": return MC_1_21;
            case "1.21.2":
            case "1.21.3":
            case "1.21.2 / 1.21.3": return MC_1_21_2;
            case "1.21.4": return MC_1_21_4;
            case "1.21.5": return MC_1_21_5;
            case "1.21.6": return MC_1_21_6;
            default: return MC_1_8;
        }
    }

    public static int getLatest() {
        return SUPPORTED_VERSION_IDS.stream().max(Comparator.naturalOrder()).orElse(0);
    }
}
