package systems.kinau.fishingbot.auth;

import java.util.Optional;

public interface IAuthenticator {

    Optional<AuthData> authenticate(String loginName, String password);
}
