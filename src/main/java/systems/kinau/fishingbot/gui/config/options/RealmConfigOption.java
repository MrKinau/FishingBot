package systems.kinau.fishingbot.gui.config.options;

import javafx.scene.control.CheckBox;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

import java.util.Optional;

public class RealmConfigOption extends ConfigOption {

    private BooleanConfigOption checkBox;

    public RealmConfigOption(String key, String description, long value, ConfigGUI configGUI) {
        super(key, description, value);

        Optional<ConfigOption> serverIP = configGUI.getConfigOption("server.ip");
        Optional<ConfigOption> serverPort = configGUI.getConfigOption("server.port");
        if (value >= 0) {
            serverIP.ifPresent(configOption -> configOption.setDisable(true));
            serverPort.ifPresent(configOption -> configOption.setDisable(true));
        }

        checkBox = new BooleanConfigOption(key + "-hidden", description, value >= 0, event -> {
            serverIP.ifPresent(configOption -> configOption.setDisable(((CheckBox)event.getSource()).isSelected()));
            serverPort.ifPresent(configOption -> configOption.setDisable(((CheckBox)event.getSource()).isSelected()));
        });
        getChildren().add(checkBox);
    }

    @Override
    public Object getValue() {
        if ((long)super.getValue() > 0 && (boolean)checkBox.getValue())
            return super.getValue();
        else if ((boolean)checkBox.getValue())
            return 0;
        else
            return -1;
    }
}
