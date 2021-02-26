package systems.kinau.fishingbot.gui.config.options;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.timer.Timer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimersConfigOption extends ConfigOption {

    private VBox root;
    private List<EditableTimer> timers = new ArrayList<>();

    public TimersConfigOption(String key, String description, List<Timer> value, Stage primaryStage) {
        super(key, description, value);
        this.root = new VBox(5);
        setValue(createValue());
        Button addTimerButton = new Button(FishingBot.getI18n().t("config-auto-timer-add-timer"));
        root.getChildren().add(addTimerButton);
        for (Timer timer : value) {
            addTimer(timer, primaryStage, false);
        }
        getChildren().add(root);

        addTimerButton.setOnAction(event -> {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("FishingBot - Config");
            inputDialog.setHeaderText(FishingBot.getI18n().t("config-auto-timer-new-timer-name"));
            Optional<String> result = inputDialog.showAndWait();
            result.ifPresent(s -> {
                Timer timer = new Timer(s, 1, TimeUnit.MINUTES, Collections.emptyList());
                addTimer(timer, primaryStage, true);
            });
        });
    }

    private void addTimer(Timer timer, Stage primaryStage, boolean expanded) {
        VBox content = new VBox(5);
        TitledPane titledPane = new TitledPane();
        titledPane.setText(timer.getName());
        titledPane.setContent(content);
        titledPane.setExpanded(expanded);

        IntegerConfigOption unitsOption = new IntegerConfigOption("auto.timer.units", FishingBot.getI18n().t("config-auto-timer-units"), timer.getUnits());
        EnumConfigOption timeUnitOption = new EnumConfigOption("auto.timer.timeunit", FishingBot.getI18n().t("config-auto-auto-timer-time-unit"), timer.getTimeUnit().name(), TimeUnit.values());
        StringArrayConfigOption commandsOption = new StringArrayConfigOption("auto.timer.commands", FishingBot.getI18n().t("config-auto-auto-command-on-respawn-commands"), timer.getCommands().toArray(new String[0]), primaryStage);
        Button deleteTimer = new Button(FishingBot.getI18n().t("config-auto-timer-delete-timer"));

        timers.add(new EditableTimer(timer, unitsOption, timeUnitOption, commandsOption));

        deleteTimer.setOnAction(event -> {
            timers.remove(getTimer(timer.getName()));
            Iterator<Node> childIter = root.getChildren().iterator();
            while (childIter.hasNext()) {
                Node curr = childIter.next();
                if (curr instanceof TitledPane) {
                    TitledPane pane = (TitledPane) curr;
                    if (pane.getText().equals(timer.getName())) {
                        childIter.remove();
                        break;
                    }
                }
            }
        });

        content.getChildren().addAll(unitsOption, timeUnitOption, commandsOption, deleteTimer);

        root.getChildren().add(root.getChildren().size() - 1, titledPane);
    }

    private String createValue() {
        JSONArray rootArray = new JSONArray();
        for (EditableTimer timer : timers) {
            // update values
            if (timer.getUnitsConfigOption() != null)
                timer.getTimer().setUnits((int)timer.getUnitsConfigOption().getValue());
            if (timer.getTimeUnitConfigOption() != null)
                timer.getTimer().setTimeUnit((TimeUnit) timer.getTimeUnitConfigOption().getValue());
            if (timer.getCommandsConfigOption() != null)
                timer.getTimer().setCommands(Arrays.asList((String[]) timer.getCommandsConfigOption().getValue()));

            JSONObject timerObj = new JSONObject();
            timerObj.put("name", timer.getTimer().getName());
            timerObj.put("units", timer.getTimer().getUnits());
            timerObj.put("timeUnit", timer.getTimer().getTimeUnit().name());
            timerObj.put("commands", timer.getTimer().getCommands());
            rootArray.add(timerObj);
        }
        return rootArray.toJSONString();
    }

    private EditableTimer getTimer(String name) {
        for (EditableTimer timer : timers) {
            if (timer.getTimer().getName().equals(name))
                return timer;
        }
        return null;
    }

    @Override
    public Object getValue() {
        return createValue();
    }

    @Data
    @AllArgsConstructor
    class EditableTimer {

        private Timer timer;
        private IntegerConfigOption unitsConfigOption;
        private EnumConfigOption timeUnitConfigOption;
        private StringArrayConfigOption commandsConfigOption;
    }

}
