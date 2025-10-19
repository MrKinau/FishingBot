package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile.DynamicProfile;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile.StaticProfile;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;


@Getter
public class ProfileComponent extends DataComponent {

    private DynamicProfile dynamicProfile;
    private StaticProfile staticProfile;
    private Optional<String> skinTexture;
    private Optional<String> capeTexture;
    private Optional<String> elytraTexture;
    private Optional<Boolean> modelType;

    public ProfileComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId >= ProtocolConstants.MC_1_21_9) {
            if (staticProfile != null) {
                out.writeBoolean(true);
                staticProfile.write(out, protocolId);
            } else {
                out.writeBoolean(false);
                dynamicProfile.write(out, protocolId);
            }
        } else {
            dynamicProfile.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (protocolId >= ProtocolConstants.MC_1_21_9) {
            if (in.readBoolean()) {
                this.staticProfile = new StaticProfile();
                this.staticProfile.read(in, protocolId);
            } else {
                this.dynamicProfile = new DynamicProfile();
                this.dynamicProfile.read(in, protocolId);
            }

            if (in.readBoolean()) {
                this.skinTexture = Optional.of(Packet.readString(in));
            } else {
                this.skinTexture = Optional.empty();
            }

            if (in.readBoolean()) {
                this.capeTexture = Optional.of(Packet.readString(in));
            } else {
                this.capeTexture = Optional.empty();
            }

            if (in.readBoolean()) {
                this.elytraTexture = Optional.of(Packet.readString(in));
            } else {
                this.elytraTexture = Optional.empty();
            }

            if (in.readBoolean()) {
                this.modelType = Optional.of(in.readBoolean());
            } else {
                this.modelType = Optional.empty();
            }
        } else {
            this.dynamicProfile = new DynamicProfile();
            this.dynamicProfile.read(in, protocolId);
        }
    }
}
