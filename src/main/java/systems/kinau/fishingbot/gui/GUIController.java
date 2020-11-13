package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.CommandExecutor;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

import javax.annotation.Resources;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIController implements Listener {

    @FXML private TableView<LootItem> lootTable;
    @FXML private TableView<Enchantment> booksTable;
    @FXML private TableView<Enchantment> bowsTable;
    @FXML private TableView<Enchantment> rodsTable;
    @FXML private TableColumn lootItemColumn;
    @FXML private TableColumn lootCountColumn;
    @FXML private TextField commandlineTextField;
    @FXML private Tab lootTab;

    @Getter private LootHistory lootHistory;

    public GUIController() {
        FishingBot.getInstance().getEventManager().registerListener(this);
        this.lootHistory = new LootHistory();
    }

    @FXML
    protected void initialize(URL location, Resources resources) {
        lootItemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lootCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        setupEnchantmentTable(booksTable);
        setupEnchantmentTable(bowsTable);
        setupEnchantmentTable(rodsTable);
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

    public void commandlineSend(Event e) {
        runCommand(commandlineTextField.getText());
        commandlineTextField.setText("");
    }

    private void runCommand(String text) {
        if (FishingBot.getInstance() == null || FishingBot.getInstance().getNet() == null)
            return;
        if (text.startsWith("/")) {
            boolean executed = FishingBot.getInstance().getCommandRegistry().dispatchCommand(text, CommandExecutor.CONSOLE);
            if (executed)
                return;
        }
        FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(text));
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
        Platform.runLater(() -> {
            lootItemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            lootCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        });

        LootItem lootItem = lootHistory.registerItem(event.getItem().getName(), event.getItem().getEnchantments());
        AtomicBoolean existing = new AtomicBoolean(false);

        if (lootTable == null)
            return;

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

        Platform.runLater(() -> {
            this.lootTab.setText(FishingBot.getI18n().t("ui-tabs-loot", lootHistory.getItems().stream().mapToInt(LootItem::getCount).sum()));
        });

        if (event.getItem().getEnchantments().isEmpty())
            return;

        Platform.runLater(() -> {
            setupEnchantmentTable(booksTable);
            setupEnchantmentTable(bowsTable);
            setupEnchantmentTable(rodsTable);
        });

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

    private void updateEnchantments(TableView<Enchantment> table, List<systems.kinau.fishingbot.network.utils.Enchantment> enchantments) {
        enchantments.forEach(enchantment -> {
            AtomicBoolean exists = new AtomicBoolean(false);
            table.getItems().forEach(item -> {
                if (item.getName().equalsIgnoreCase(enchantment.getEnchantmentType().getName()) && item.getLevel() == enchantment.getLevel()) {
                    item.setCount(item.getCount() + 1);
                    exists.set(true);
                    Platform.runLater(() -> {
                        table.getColumns().get(2).setVisible(false);
                        table.getColumns().get(2).setVisible(true);
                    });
                }
            });
            if (!exists.get())
                table.getItems().add(new Enchantment(enchantment.getEnchantmentType().getName(), enchantment.getLevel(), 1));
        });
    }
}
