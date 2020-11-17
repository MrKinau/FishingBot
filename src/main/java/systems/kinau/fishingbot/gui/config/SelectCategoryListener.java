package systems.kinau.fishingbot.gui.config;

import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class SelectCategoryListener implements EventHandler<MouseEvent> {

    private ConfigGUI configGUI;

    public SelectCategoryListener(ConfigGUI configGUI) {
        this.configGUI = configGUI;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getSource() instanceof ListView) {
            ListView<ConfigGUI.ConfigCategory> listView = (ListView<ConfigGUI.ConfigCategory>) event.getSource();
            String category = listView.getSelectionModel().getSelectedItem().getKey();
            configGUI.configOptionCategoryTitle.setText(listView.getSelectionModel().getSelectedItem().getTranslation());
            configGUI.configOptionsBox.getChildren().clear();
            if (configGUI.configOptions.containsKey(category)) {
                configGUI.configOptionsBox.getChildren().addAll(configGUI.configOptions.get(category));
            }
        }
    }

}
