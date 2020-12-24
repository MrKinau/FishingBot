package systems.kinau.fishingbot.gui.config.options;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;

public class ActivateableTitledPaneOption extends TitledPaneOption {

    @Getter boolean enabled;

    public ActivateableTitledPaneOption(String key, String title, VBox content, boolean enabled) {
        super(key, title, content);
        this.enabled = enabled;

        content.getChildren().add(0, new BooleanConfigOption(key + ".enabled", FishingBot.getI18n().t("ui-config-enabled"), enabled, event -> {
            this.enabled = !isEnabled();
            updateDisabledStatus();
        }));

        content.getChildren().addListener((ListChangeListener<? super Node>) c -> {
            updateDisabledStatus();
        });
    }

    private void updateDisabledStatus() {
        getContent().getChildren().forEach(node -> {
            if (node instanceof BooleanConfigOption && ((BooleanConfigOption) node).getDescription().equals(FishingBot.getI18n().t("ui-config-enabled")))
                return;
            node.setDisable(!isEnabled());
        });
    }

}
