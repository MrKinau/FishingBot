package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.realms.Realm;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Dialogs {

    public static void showCredentialsNotSet() {
        setupJFX();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(FishingBot.NAME_AND_VERSION);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-credentials-header"));
            alert.setContentText(FishingBot.getI18n().t("dialog-credentials-content"));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static void showJavaFXNotWorking() {
        JOptionPane.showConfirmDialog(new JFrame(), "JavaFX seems to be not working properly on your computer!\nPlease look at the log.\n\n" +
                "You can still use the bot in headless (nogui) mode using the start argument -nogui.", "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showRealmsWorlds(List<Realm> possibleRealms, Consumer<Realm> callback) {
        setupJFX();
        List<String> realmNames = possibleRealms.stream().map(realm -> realm.getName() + " by " + realm.getOwner()).collect(Collectors.toList());
        Platform.runLater(() -> {
            Dialog dialog;
            if (possibleRealms.isEmpty()) {
                dialog = new Alert(Alert.AlertType.INFORMATION);

                dialog.setHeaderText(FishingBot.getI18n().t("dialog-realms-no-realms"));
                dialog.setContentText(FishingBot.getI18n().t("realms-no-realms"));
            } else {
                dialog = new ChoiceDialog<>(realmNames.get(0), realmNames);
                dialog.setHeaderText(FishingBot.getI18n().t("dialog-realms-select"));
            }

            dialog.setTitle(FishingBot.NAME_AND_VERSION);

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            AtomicReference<Realm> returningRealm = new AtomicReference<>(null);

            try {
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(s -> {
                    String name = s.split(" by ")[0];
                    String owner = s.split(" by ")[1];
                    Optional<Realm> optRealm = possibleRealms.stream()
                            .filter(realm -> realm.getName().equals(name))
                            .filter(realm -> realm.getOwner().equals(owner))
                            .findAny();
                    optRealm.ifPresent(returningRealm::set);
                });
            } catch (Throwable ignore) {
            }
            callback.accept(returningRealm.get());
        });
    }

    public static void showRealmsAcceptToS(Consumer<Boolean> callback) {
        setupJFX();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
            alert.setTitle(FishingBot.NAME_AND_VERSION);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-realms-tos-header"));
            alert.setContentText(FishingBot.getI18n().t("dialog-realms-tos-content", "https://www.minecraft.net/en-us/realms/terms"));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            Optional<ButtonType> buttonType = alert.showAndWait();
            buttonType.ifPresent(buttonType1 -> callback.accept(buttonType1 == ButtonType.YES));
        });
    }

    private static void setupJFX() {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException ignore) {
        }
    }

    public static void showAboutWindow(Stage parent, Consumer<String> callBack) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.NAME_AND_VERSION);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-about-header"));
            FlowPane fp = new FlowPane();
            Label lbl = new Label(FishingBot.getI18n().t("dialog-about-content"));
            Hyperlink link = new Hyperlink(" faithful.team");
            fp.getChildren().addAll(lbl, link);

            link.setOnAction(event -> {
                alert.close();
                callBack.accept("https://faithful.team/");
            });

            alert.getDialogPane().contentProperty().set(fp);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.initOwner(parent);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static void showCredentialsInvalid(Consumer<String> callBack) {
        setupJFX();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.NAME_AND_VERSION);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-credentials-invalid-header"));
            FlowPane fp = new FlowPane();
            Label lbl = new Label(FishingBot.getI18n().t("dialog-credentials-invalid-content"));
            Hyperlink link = new Hyperlink(" FishingBot Wiki");
            fp.getChildren().addAll(lbl, link);

            link.setOnAction(event -> {
                alert.close();
                callBack.accept("https://github.com/MrKinau/FishingBot/wiki/Troubleshooting");
            });

            alert.getDialogPane().contentProperty().set(fp);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static boolean showAcceptConnection(String ip) {
        setupJFX();

        AtomicReference<Boolean> dialogClicked = new AtomicReference<>(null);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
            alert.setTitle(FishingBot.NAME_AND_VERSION);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-proxy-accept-connection-header"));
            alert.setContentText(FishingBot.getI18n().t("dialog-proxy-accept-connection-content", ip));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            Optional<ButtonType> buttonType = alert.showAndWait();
            buttonType.ifPresent(buttonType1 -> dialogClicked.set(buttonType1 == ButtonType.YES));
        });
        // wait for click
        while (dialogClicked.get() == null) {
        }

        return dialogClicked.get();
    }

    public static boolean showAcceptConnectionNoGui(String ip) {
        Scanner s = new Scanner(System.in);

        boolean accepted = false;
        System.err.print(FishingBot.getI18n().t("dialog-proxy-accept-connection-content", ip) + ": (yes/No): ");
        String entered = s.nextLine();
        if (entered.equalsIgnoreCase("y") || entered.equalsIgnoreCase("yes")) {
            accepted = true;
        }

        return accepted;
    }
}
