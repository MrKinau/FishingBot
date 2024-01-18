package systems.kinau.fishingbot.gui.config.options;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

public abstract class ConfigOption extends HBox {

    @Getter private ConfigGUI configGui;
    @Getter private String description;
    @Getter private String key;
    @Getter @Setter private Object value;

    public ConfigOption(ConfigGUI configGui, String key, String description, Object value) {
        super(10);
        this.configGui = configGui;
        this.key = key;
        this.description = description;
        this.value = value;

        setAlignment(Pos.CENTER_LEFT);
    }

    public void updateValue() {
        // may be implemented by nodes
    }
}
