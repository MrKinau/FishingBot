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
        JSONArray rootArray = new JSONArray();
        for (EditableEjectionRule ejectionRule : ejectionRules) {
            // update values
            if (ejectionRule.getDirectionOption() != null)
                ejectionRule.getEjectionRule().setDirection((LocationUtils.Direction) ejectionRule.getDirectionOption().getValue());
            if (ejectionRule.getEjectionTypeOption() != null)
                ejectionRule.getEjectionRule().setEjectionType((EjectionRule.EjectionType) ejectionRule.getEjectionTypeOption().getValue());
            if (ejectionRule.getItemListOption() != null)
                ejectionRule.getEjectionRule().setAllowList(Arrays.asList((String[]) ejectionRule.getItemListOption().getValue()));

            JSONObject ruleObj = new JSONObject();
            ruleObj.put("name", ejectionRule.getEjectionRule().getName());
            ruleObj.put("direction", ejectionRule.getEjectionRule().getDirection().name());
            ruleObj.put("allowList", ejectionRule.getEjectionRule().getAllowList());
            ruleObj.put("ejectionType", ejectionRule.getEjectionRule().getEjectionType().name());
            rootArray.add(ruleObj);
        }
        return rootArray.toJSONString();
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
