package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIController implements Listener {

    @FXML private TableView<LootItem> lootTable;
    @FXML private TableView<Enchantment> booksTable;
    @FXML private TableView<Enchantment> bowsTable;
    @FXML private TableView<Enchantment> rodsTable;
    @FXML private TableColumn lootItemColumn;
    @FXML private TableColumn lootCountColumn;

    @Getter private LootHistory lootHistory;

    public GUIController() {
        FishingBot.getInstance().getEventManager().registerListener(this);
        this.lootHistory = new LootHistory();
    }

    public void exit(Event e) {
        Platform.exit();
    }

    public void deleteAllData(Event e) {
        this.lootHistory = new LootHistory();
        lootTable.getItems().clear();
        booksTable.getItems().clear();
        bowsTable.getItems().clear();
        rodsTable.getItems().clear();
    }

    public void github(Event e) {
        openWebpage("https://github.com/MrKinau/FishingBot");
    }

    public void issues(Event e) {
        openWebpage("https://github.com/MrKinau/FishingBot/issues");
    }

    public void discord(Event e) {
        openWebpage("https://discord.gg/xHpCDYf");
    }

    public void openConfig(Event e) {
        openFile(FishingBot.getInstance().getConfig().getPath());
    }

    public void openLogsDir(Event e) {
        openFile(FishingBot.getInstance().getLogsFolder().getPath());
    }

    public void openLog(Event e) {
        openFile(FishingBot.getInstance().getLogsFolder().getPath() + "/log0.log");
    }

    private void openFile(String fileUrl) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(fileUrl));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openWebpage(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onFishCaught(FishCaughtEvent event) {
        lootItemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lootCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        LootItem lootItem = lootHistory.registerItem(event.getItem().getName(), event.getItem().getEnchantments());
        AtomicBoolean existing = new AtomicBoolean(false);
        lootTable.getItems().forEach(item -> {
            if (item.getName().equalsIgnoreCase(lootItem.getName())) {
                item.setCount(lootItem.getCount());
                existing.set(true);
                Platform.runLater(() -> {
                    lootCountColumn.setVisible(false);
                    lootCountColumn.setVisible(true);
                });
            }
        });
        if (!existing.get())
            lootTable.getItems().add(lootItem);

        if (event.getItem().getEnchantments().isEmpty())
            return;

        setupEnchantmentTable(booksTable);
        setupEnchantmentTable(bowsTable);
        setupEnchantmentTable(rodsTable);

        switch (event.getItem().getName().toLowerCase()) {
            case "enchanted_book": {
                updateEnchantments(booksTable, event.getItem().getEnchantments());
                break;
            }
            case "bow": {
                updateEnchantments(bowsTable, event.getItem().getEnchantments());
                break;
            }
            case "fishing_rod": {
                updateEnchantments(rodsTable, event.getItem().getEnchantments());
                break;
            }
        }
    }

    private void setupEnchantmentTable(TableView<Enchantment> table) {
        table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("level"));
        table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    private void updateEnchantments(TableView<Enchantment> table, List<Map<String, Short>> enchantments) {
        enchantments.forEach(enchantment -> {
            AtomicBoolean exists = new AtomicBoolean(false);
            table.getItems().forEach(item -> {
                enchantment.keySet().forEach(enchantmentName -> {
                    if (item.getName().equalsIgnoreCase(enchantmentName) && item.getLevel() == enchantment.get(enchantmentName)) {
                        item.setCount(item.getCount() + 1);
                        exists.set(true);
                        Platform.runLater(() -> {
                            table.getColumns().get(2).setVisible(false);
                            table.getColumns().get(2).setVisible(true);
                        });
                    }
                });
            });
            if (!exists.get())
                enchantment.keySet().forEach(s -> table.getItems().add(new Enchantment(s, enchantment.get(s), 1)));
        });
    }
}
