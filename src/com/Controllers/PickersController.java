package com.Controllers;

import com.BaerMA.DataObjects.PickerGenerationObject;
import com.BaerMA.MainStage;
import com.BaerMA.DataObjects.PickerObject;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PickersController implements Initializable {

    //Add Picker Objects
    public ListView<PickerObject> PListView;
    public Button PDeleteButton;
    public PickerObject selectedObject;
    public TextField AddPickerField;

    //Generation Pickers
    public ListView<PickerGenerationObject> PGenerationListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeAddPickerList();
    }

    private void initializeAddPickerList(){
        PListView.getItems().addAll(MainStage.settings.pickers);
        PListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        PListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends PickerObject> observable, PickerObject oldValue, PickerObject newValue) -> {
            if(observable!=null){
                PDeleteButton.setDisable(false);
                selectedObject=observable.getValue();
                System.out.println(selectedObject);
            }
        });
    }

    private void initializePickerGenerationList(){
        PGenerationListView.getItems().addAll(MainStage.pickers.getPickersForGeneration(0));
        PGenerationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


    public void addPicker(){
        PickerObject picker = new PickerObject(AddPickerField.getText());
        PListView.getItems().add(picker);
        MainStage.settings.pickers.add(picker);
        MainStage.settings.save();
    }

    public void deletePicker(){
        PickerObject picker = PListView.getSelectionModel().getSelectedItem();
        PListView.getItems().remove(picker);
        MainStage.settings.pickers.remove(picker);
        System.out.println("PlistView: "+PListView.getItems().size()+" settings: "+MainStage.settings.pickers.size());
        MainStage.settings.save();

    }
}
