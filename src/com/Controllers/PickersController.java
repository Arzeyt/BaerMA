package com.Controllers;

import com.BaerMA.MainStage;
import com.BaerMA.DataObjects.PickerObject;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PickersController implements Initializable {

    //Add Picker Objects
    public ListView<PickerObject> PListView;
    public Button PDeleteButton;
    public PickerObject selectedObject;
    public TextField AddPickerField;
    public Button PAddToGenButton;

    //Generation Pickers
    public Spinner<Integer> PGenerationSpinner;
    public ListView<PickerObject> PGenerationListView;

    //variables

    public void addPickerToGeneration(int generation, PickerObject picker){
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeAddPickerList();
        initializePickerGenerationList();
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
        PGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,1000,0) {});
        PGenerationSpinner.setEditable(true);
        PGenerationSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            PAddToGenButton.setText("Add to Gen: "+PGenerationSpinner.getValue());
            PGenerationListView.getItems().clear();
            PGenerationListView.getItems().addAll(MainStage.pickerGenerationMapData.getPickersForGen(observable.getValue()));
        });

        PGenerationListView.getItems().addAll(MainStage.pickerGenerationMapData.getPickersForGen(PGenerationSpinner.getValue()));
        PGenerationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void addToGenButtonPressed(){
        PickerObject selected = PListView.getFocusModel().getFocusedItem();
        MainStage.pickerGenerationMapData.addPickerToGeneration(selected, PGenerationSpinner.getValue());
        PGenerationListView.getItems().add(selected);
    }

    public void removeFromGenButtonPressed(){
        PickerObject selected = PGenerationListView.getFocusModel().getFocusedItem();
        MainStage.pickerGenerationMapData.removePickerFromGeneration(selected, PGenerationSpinner.getValue());
        PGenerationListView.getItems().remove(selected);
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
