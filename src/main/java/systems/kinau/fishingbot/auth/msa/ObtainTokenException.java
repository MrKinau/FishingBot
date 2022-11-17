package systems.kinau.fishingbot.auth.msa;

import lombok.Getter;

public class ObtainTokenException extends IllegalArgumentException {

    @Getter private RefreshTokenResult reason;

    public ObtainTokenException(RefreshTokenResult reason) {
        super(reason.name());
        this.reason = reason;
    }
}
