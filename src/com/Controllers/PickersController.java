package com.Controllers;

import com.BaerMA.MainStage;
import com.BaerMA.PickerObject;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class PickersController implements Initializable {

    public ListView<PickerObject> PListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeListView();
    }

    private void initializeListView(){
        PListView.getItems().addAll(MainStage.settings.pickers);
    }

    
}
