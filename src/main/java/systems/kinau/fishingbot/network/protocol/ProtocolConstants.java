/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolConstants {

    public static final List<Integer> SUPPORTED_VERSION_IDS = new ArrayList<>();
    public static final Map<String, Integer> VERSION_STRING_TO_PVN = new HashMap<>();
    public static final Map<Integer, String> PVN_TO_VERSION_STRING = new HashMap<>();
    public static final Map<Integer, String> PVN_TO_LATEST_VERSION_STRING = new HashMap<>();
    public static final Map<Integer, String> PVN_TO_FIRST_VERSION_STRING = new HashMap<>();

    public static final int AUTOMATIC = -1;
    static {
        SUPPORTED_VERSION_IDS.add(AUTOMATIC);
        VERSION_STRING_TO_PVN.put("AUTOMATIC", AUTOMATIC);
        PVN_TO_VERSION_STRING.put(AUTOMATIC, "AUTOMATIC");
    }

    public static final int MC_1_8 = registerVersion(47, "1.8");
    public static final int MC_1_9 = registerVersion(107, "1.9");
    public static final int MC_1_9_1 = registerVersion(108, "1.9.1");
    public static final int MC_1_9_2 = registerVersion(109, "1.9.2");
    public static final int MC_1_9_4 = registerVersion(110, "1.9.4");
    public static final int MC_1_10 = registerVersion(210, "1.10");
    public static final int MC_1_11 = registerVersion(315, "1.11");
    public static final int MC_1_11_1 = registerVersion(316, "1.11.1");
    public static final int MC_1_12 = registerVersion(335, "1.12");
    public static final int MC_1_12_1 = registerVersion(338, "1.12.1");
    public static final int MC_1_12_2 = registerVersion(340, "1.12.2");
    public static final int MC_1_13 = registerVersion(393, "1.13");
    public static final int MC_1_13_1 = registerVersion(401, "1.13.1");
    public static final int MC_1_13_2 = registerVersion(404, "1.13.2");
    public static final int MC_1_14 = registerVersion(477, "1.14");
    public static final int MC_1_14_1 = registerVersion(480, "1.14.1");
    public static final int MC_1_14_2 = registerVersion(485, "1.14.2");
    public static final int MC_1_14_3 = registerVersion(490, "1.14.3");
    public static final int MC_1_14_4 = registerVersion(498, "1.14.4");
    public static final int MC_1_15 = registerVersion(573, "1.15");
    public static final int MC_1_15_1 = registerVersion(575, "1.15.1");
    public static final int MC_1_15_2 = registerVersion(578, "1.15.2");
    public static final int MC_1_16 = registerVersion(735, "1.16");
    public static final int MC_1_16_1 = registerVersion(736, "1.16.1");
    public static final int MC_1_16_2 = registerVersion(751, "1.16.2");
    public static final int MC_1_16_3 = registerVersion(753, "1.16.3");
    public static final int MC_1_16_4 = registerVersion(754, "1.16.4", "1.16.5");
    public static final int MC_1_17 = registerVersion(755, "1.17");
    public static final int MC_1_17_1 = registerVersion(756, "1.17.1");
    public static final int MC_1_18 = registerVersion(757, "1.18", "1.18.1");
    public static final int MC_1_18_2 = registerVersion(758, "1.18.2");
    public static final int MC_1_19 = registerVersion(759, "1.19");
    public static final int MC_1_19_1 = registerVersion(760, "1.19.1", "1.19.2");
    public static final int MC_1_19_3 = registerVersion(761, "1.19.3");
    public static final int MC_1_19_4 = registerVersion(762, "1.19.4");
    public static final int MC_1_20 = registerVersion(763, "1.20", "1.20.1");
    public static final int MC_1_20_2 = registerVersion(764, "1.20.2");
    public static final int MC_1_20_3 = registerVersion(765, "1.20.3", "1.20.4");
    public static final int MC_1_20_5 = registerVersion(766, "1.20.5", "1.20.6");
    public static final int MC_1_21 = registerVersion(767, "1.21", "1.21.1");
    public static final int MC_1_21_2 = registerVersion(768, "1.21.2", "1.21.3");
    public static final int MC_1_21_4 = registerVersion(769, "1.21.4");
    public static final int MC_1_21_5 = registerVersion(770, "1.21.5");
    public static final int MC_1_21_6 = registerVersion(771, "1.21.6");
    public static final int MC_1_21_7 = registerVersion(772, "1.21.7", "1.21.8");
    public static final int MC_1_21_9 = registerVersion(773, "1.21.9", "1.21.10");

    public static int getSnapshotVersion(int snapshotNumber) {
        return (1 << 30) | snapshotNumber;
    }

    public static int registerSnapshotVersion(int snapshotNumber, String... versionStrings) {
        return registerVersion(getSnapshotVersion(snapshotNumber), versionStrings);
    }

    public static int registerVersion(int protocolId, String... versionStrings) {
        SUPPORTED_VERSION_IDS.add(protocolId);
        if (versionStrings == null || versionStrings.length == 0) throw new IllegalArgumentException("protocol id " + protocolId + " has no version string");
        for (String versionString : versionStrings) {
            VERSION_STRING_TO_PVN.put(versionString, protocolId);
            PVN_TO_VERSION_STRING.put(protocolId, versionString);
            PVN_TO_LATEST_VERSION_STRING.put(protocolId, versionString);
            if (!PVN_TO_FIRST_VERSION_STRING.containsKey(protocolId))
                PVN_TO_FIRST_VERSION_STRING.put(protocolId, versionString);
        }
        if (versionStrings.length > 1) {
            String merged = String.join(" / ", versionStrings);
            VERSION_STRING_TO_PVN.put(merged, protocolId);
            PVN_TO_VERSION_STRING.put(protocolId, merged);
        }
        return protocolId;
    }

    public static String getVersionString(int protocolId) {
        return PVN_TO_VERSION_STRING.getOrDefault(protocolId, "Unknown version");
    }

    public static String getLatestVersionStringByPVN(int protocolId) {
        return PVN_TO_LATEST_VERSION_STRING.getOrDefault(protocolId, getVersionString(protocolId));
    }

    public static String getFirstVersionStringByPVN(int protocolId) {
        return PVN_TO_FIRST_VERSION_STRING.getOrDefault(protocolId, getVersionString(protocolId));
    }

    public static int getProtocolId(String versionString) {
        return VERSION_STRING_TO_PVN.getOrDefault(versionString, MC_1_8);
    }

    public static int getLatest() {
        return SUPPORTED_VERSION_IDS.stream().max(Comparator.naturalOrder()).orElse(0);
    }
}
