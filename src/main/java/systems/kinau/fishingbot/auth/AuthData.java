/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthData {

    @Getter private String accessToken;
    @Getter private String clientToken;
    @Getter private String profile;
    @Getter private String username;
}
