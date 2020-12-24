package systems.kinau.fishingbot.gui.config.options;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Optional;

public class TitledPaneOption extends ConfigOption {

    @Getter private TitledPane titledPane;
    @Getter private VBox content;

    public TitledPaneOption(String key, String title, VBox content) {
        super(key, title, content);
        this.content = content;
        this.titledPane = new TitledPane(title, content);

        titledPane.setCollapsible(false);

        getChildren().add(titledPane);
    }

    public boolean contains(String key) {
        return getContent().getChildren().stream()
                .filter(node -> node instanceof ConfigOption)
                .map(node -> (ConfigOption)node)
                .anyMatch(configOption -> configOption.getKey().equals(key));
    }

    public Optional<ConfigOption> get(String key) {
        return getContent().getChildren().stream()
                .filter(node -> node instanceof ConfigOption)
                .map(node -> (ConfigOption)node)
                .filter(configOption -> configOption.getKey().equals(key))
                .findAny();
    }

}
