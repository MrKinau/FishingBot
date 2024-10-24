package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim.TrimMaterial;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim.TrimPattern;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class TrimComponent extends DataComponent {

    private TrimMaterial trimMaterial;
    private TrimPattern trimPattern;
    private boolean showInTooltip;

    public TrimComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        trimMaterial.write(out, protocolId);
        trimPattern.write(out, protocolId);
        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        TrimMaterial trimMaterial = new TrimMaterial();
        trimMaterial.read(in, protocolId);
        this.trimMaterial = trimMaterial;

        TrimPattern trimPattern = new TrimPattern();
        trimPattern.read(in, protocolId);
        this.trimPattern = trimPattern;

        this.showInTooltip = in.readBoolean();
    }
}
