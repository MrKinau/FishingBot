package systems.kinau.fishingbot.auth.msa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created: 22.09.2021
 *
 * @author Summerfeeling
 */
@RequiredArgsConstructor
@ToString
@Getter
public class DeviceTokenCallback {

    private final String userCode;
    private final String deviceCode;
    private final String verificationUrl;
    private final int expiresIn;
    private final int interval;

}
