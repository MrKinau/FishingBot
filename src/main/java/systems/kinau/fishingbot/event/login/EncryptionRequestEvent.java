/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.security.PublicKey;

@Getter
@AllArgsConstructor
public class EncryptionRequestEvent extends Event {

    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken;
    private boolean shouldAuthenticate;

}
