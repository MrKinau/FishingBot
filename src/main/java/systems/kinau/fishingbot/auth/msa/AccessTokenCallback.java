package systems.kinau.fishingbot.auth.msa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created: 22.09.2021
 *
 * @author Summerfeeling
 */
@AllArgsConstructor
@ToString
@Getter
public class AccessTokenCallback {

    private final String accessToken;
    private final String refreshToken;

}
