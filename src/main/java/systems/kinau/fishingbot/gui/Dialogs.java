package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.msa.RefreshTokenResult;
import systems.kinau.fishingbot.network.mojangapi.Realm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Dialogs {

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

            dialog.setTitle(FishingBot.TITLE);

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
            alert.setTitle(FishingBot.TITLE);

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

    public static void showAboutWindow(Stage parent) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.TITLE);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-about-header"));
            FlowPane fp = new FlowPane();
            Label lbl = new Label(FishingBot.getI18n().t("dialog-about-content"));
            Hyperlink link = new Hyperlink(" faithful.team");
            fp.getChildren().addAll(lbl, link);

            link.setOnAction(event -> {
                alert.close();
                GUIController.openWebpage("https://faithful.team/");
            });

            alert.getDialogPane().contentProperty().set(fp);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.initOwner(parent);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static void showCredentialsInvalid() {
        setupJFX();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.TITLE);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-credentials-invalid-header"));
            FlowPane fp = new FlowPane();
            Label lbl = new Label(FishingBot.getI18n().t("dialog-credentials-invalid-content"));
            Hyperlink link = new Hyperlink(" FishingBot Wiki");
            fp.getChildren().addAll(lbl, link);

            link.setOnAction(event -> {
                alert.close();
                GUIController.openWebpage("https://github.com/MrKinau/FishingBot/wiki/Troubleshooting");
            });

            alert.getDialogPane().contentProperty().set(fp);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static CompletableFuture<Alert> showAuthorizationRequest(String code, String url) {
        setupJFX();

        CompletableFuture<Alert> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(FishingBot.TITLE);
            alert.getButtonTypes().setAll(ButtonType.CANCEL);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-authorization-header"));
            FlowPane flowPane = new FlowPane();

            List<Text> texts = new ArrayList<>();
            String rawMsg = FishingBot.getI18n().t("auth-create-refresh-token", "%s", "%s");
            for (String s : rawMsg.split("%s")) {
                texts.add(new Text(s));
            }

            Hyperlink link = new Hyperlink(url);
            link.setOnAction(event -> {
                GUIController.openWebpage(url);
            });

            TextField codeText = new TextField(code);
            codeText.setEditable(false);
            codeText.setPrefWidth(95);
            codeText.setAlignment(Pos.CENTER);

            TextFlow flow = new TextFlow();
            if (texts.size() >= 1)
                flow.getChildren().add(texts.get(0));
            flow.getChildren().add(codeText);
            if (texts.size() >= 2)
                flow.getChildren().add(texts.get(1));
            flow.getChildren().add(link);
            if (texts.size() >= 3)
                flow.getChildren().add(texts.get(2));

            VBox spacer = new VBox();
            spacer.setMinHeight(80);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(810);

            flowPane.getChildren().addAll(flow, spacer, progressBar);

            alert.getDialogPane().contentProperty().set(flowPane);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            future.complete(alert);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData().isCancelButton()) {
                    FishingBot.getInstance().getCurrentBot().setPreventStartup(true);
                    FishingBot.getInstance().interruptMainThread();
                    FishingBot.getLog().info("Authentication canceled!");
                }
            }
        });
        return future;
    }

    public static void showAuthFailed(RefreshTokenResult reason) {
        setupJFX();

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(FishingBot.TITLE);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-authorization-failed-header"));
            FlowPane flowPane = new FlowPane();

            String msg = FishingBot.getI18n().t("auth-failed-" + reason.name().replace("_", "-").toLowerCase());
            Text text = new Text(msg);

            flowPane.getChildren().add(text);

            alert.getDialogPane().contentProperty().set(flowPane);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.show();
        });
    }
}
