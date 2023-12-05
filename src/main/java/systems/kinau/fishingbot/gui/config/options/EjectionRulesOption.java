package systems.kinau.fishingbot.gui.config.options;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.ejection.EjectionRule;
import systems.kinau.fishingbot.utils.LocationUtils;

import java.util.*;

public class EjectionRulesOption extends ConfigOption {

    private VBox root;
    private List<EditableEjectionRule> ejectionRules;

    public EjectionRulesOption(String key, String description, List<EjectionRule> value, Stage primaryStage) {
        super(key, description, value);
        this.ejectionRules = new ArrayList<>();
        for (EjectionRule ejectionRule : value) {
            ejectionRules.add(new EditableEjectionRule(ejectionRule, null, null, null));
        }
        this.root = new VBox(5);
        setValue(createValue());
        Button addRuleButton = new Button(FishingBot.getI18n().t("config-auto-auto-eject-add-rule"));
        root.getChildren().add(addRuleButton);
        for (EditableEjectionRule ejectionRule : ejectionRules) {
            addRule(ejectionRule.getEjectionRule(), primaryStage, false);
        }
        getChildren().add(root);

        addRuleButton.setOnAction(event -> {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("FishingBot - Config");
            inputDialog.setHeaderText(FishingBot.getI18n().t("config-auto-auto-eject-new-rule-name"));
            Optional<String> result = inputDialog.showAndWait();
            result.ifPresent(s -> {
                EjectionRule ejectionRule = new EjectionRule(s, LocationUtils.Direction.SOUTH, new ArrayList<>(), EjectionRule.EjectionType.DROP);
                ejectionRules.add(new EditableEjectionRule(ejectionRule, null, null, null));
                addRule(ejectionRule, primaryStage, true);
            });
        });
    }

    private void addRule(EjectionRule ejectionRule, Stage primaryStage, boolean expanded) {
        VBox content = new VBox(5);
        TitledPane titledPane = new TitledPane();
        titledPane.setText(ejectionRule.getName());
        titledPane.setContent(content);
        titledPane.setExpanded(expanded);

        EnumConfigOption directionOption = new EnumConfigOption("auto.auto-eject.rules.direction", FishingBot.getI18n().t("config-auto-auto-eject-direction"), ejectionRule.getDirection().name(), LocationUtils.Direction.values());
        EnumConfigOption ejectionType = new EnumConfigOption("auto.auto-eject.rules.ejection-type", FishingBot.getI18n().t("config-auto-auto-eject-ejection-type"), ejectionRule.getEjectionType().name(), EjectionRule.EjectionType.values());
        StringArrayConfigOption allowListOption = new StringArrayConfigOption("auto.auto-eject.rules.list", FishingBot.getI18n().t("config-auto-auto-eject-list"), ejectionRule.getAllowList().toArray(new String[0]), primaryStage);
        Button deleteRule = new Button(FishingBot.getI18n().t("config-auto-auto-eject-delete-rule"));

        deleteRule.setOnAction(event -> {
            ejectionRules.remove(getRule(ejectionRule.getName()));
            Iterator<Node> childIter = root.getChildren().iterator();
            while (childIter.hasNext()) {
                Node curr = childIter.next();
                if (curr instanceof TitledPane) {
                    TitledPane pane = (TitledPane) curr;
                    if (pane.getText().equals(ejectionRule.getName())) {
                        childIter.remove();
                        break;
                    }
                }
            }
        });

        EditableEjectionRule editRule = getRule(ejectionRule.getName());
        if (editRule != null) {
            editRule.setDirectionOption(directionOption);
            editRule.setEjectionTypeOption(ejectionType);
            editRule.setItemListOption(allowListOption);
        }

        content.getChildren().addAll(directionOption, ejectionType, allowListOption, deleteRule);

        root.getChildren().add(root.getChildren().size() - 1, titledPane);
    }

    private String createValue() {
        JsonArray rootArray = new JsonArray();
        for (EditableEjectionRule ejectionRule : ejectionRules) {
            // update values
            if (ejectionRule.getDirectionOption() != null)
                ejectionRule.getEjectionRule().setDirection((LocationUtils.Direction) ejectionRule.getDirectionOption().getValue());
            if (ejectionRule.getEjectionTypeOption() != null)
                ejectionRule.getEjectionRule().setEjectionType((EjectionRule.EjectionType) ejectionRule.getEjectionTypeOption().getValue());
            if (ejectionRule.getItemListOption() != null)
                ejectionRule.getEjectionRule().setAllowList(Arrays.asList((String[]) ejectionRule.getItemListOption().getValue()));

            JsonObject ruleObj = new JsonObject();
            ruleObj.addProperty("name", ejectionRule.getEjectionRule().getName());
            ruleObj.addProperty("direction", ejectionRule.getEjectionRule().getDirection().name());
            JsonArray allowList = new JsonArray();
            ejectionRule.getEjectionRule().getAllowList().forEach(allowList::add);
            ruleObj.add("allowList", allowList);
            ruleObj.addProperty("ejectionType", ejectionRule.getEjectionRule().getEjectionType().name());
            rootArray.add(ruleObj);
        }
        return rootArray.toString();
    }

    private EditableEjectionRule getRule(String name) {
        for (EditableEjectionRule ejectionRule : ejectionRules) {
            if (ejectionRule.getEjectionRule().getName().equals(name))
                return ejectionRule;
        }
        return null;
    }

    @Override
    public Object getValue() {
        return createValue();
    }

    @Data
    @AllArgsConstructor
    class EditableEjectionRule {

        private EjectionRule ejectionRule;
        private EnumConfigOption directionOption;
        private EnumConfigOption ejectionTypeOption;
        private StringArrayConfigOption itemListOption;
    }

}
