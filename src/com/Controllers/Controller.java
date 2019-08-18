package com.Controllers;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import com.BaerMA.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller implements Initializable{


    @FXML private Spinner<Integer> experimentalGenerationSpinner;
    @FXML private DatePicker experimentalGenerationDatePicker;
    @FXML private Spinner<Integer> sampleNumberSpinner;
    @FXML private CheckBox ExtinctButton;
    @FXML private Spinner<Integer> backupGenerationSpinner;
    @FXML private DatePicker backupOfDatePicker;
    @FXML private TextArea notesField;

    @FXML private TableView allEntriesTable;
    @FXML private TableColumn AESampleColumn,AEExperimentalGenerationColumn,AEPickDateColumn,AEBackupGenerationColumn,AEBackupDateColumn,AENotesColumn;

    @FXML private Spinner<Integer> SLGenerationSpinner;
    @FXML private TableView sampleListTable;
    @FXML private TableColumn SLSampleColumn,SLGenerationColumn, SLBackupCountColumn, SLResetCountColumn;

    @FXML private Spinner<Integer> EHSampleNumberSpinner;
    @FXML private TableView EHTable;
    @FXML private TableColumn EHSampleColumn, EHExperimentalGenerationColumn, EHPickDateColumn, EHBackupGenerationColumn, EHBackupDateColumn,EHNotesColumn;

    public Entries entriesClass = new Entries();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entriesClass.parseEntriesJSON();
        initializeAddEntryComponents();
        initializeAllEntriesTable();
        initializeSampleListTable();
        initializeSampleHistoryTable();

    }

    /**
     * Assigns spinner value factories and uses the focusedProperty event listener to enable reading accurate inputs. This is a work-around to a bug
     */
    private void initializeAddEntryComponents() {
        experimentalGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,999,0));
        experimentalGenerationSpinner.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue){
                experimentalGenerationSpinner.increment(0  );
            }
        }));

        sampleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(500,999,500));
        sampleNumberSpinner.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue){
                sampleNumberSpinner.increment(0  );
            }
        }));

        backupGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,500,0));
        backupGenerationSpinner.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue){
                backupGenerationSpinner.increment(0);
            }
        }));

    }

    private void initializeAllEntriesTable() {
        AESampleColumn.setCellValueFactory(new PropertyValueFactory<Entry,Integer>("sampleIDssp"));
        AEExperimentalGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry,Integer>("SexperimentalGeneration"));
        AEPickDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        AEBackupGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SbackupGeneration"));
        AEBackupDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        AENotesColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));

        allEntriesTable.setItems(entriesClass.entriesList);
    }

    private void initializeSampleListTable() {
        SLSampleColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.sampleIDString));
        SLGenerationColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.calculatedGenerationString));
        SLBackupCountColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.backupCountString));
        SLResetCountColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.resetCountString));

        sampleListTable.setItems(entriesClass.calculatedEntries);

        SLGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,999,0));
        SLGenerationSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            entriesClass.createCalculatedEntriesList(newValue);
        });
    }

    private void initializeSampleHistoryTable(){

        EHSampleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(500,999,500));
        EHSampleNumberSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            entriesClass.createEntryHistoryList(newValue);
        });

        EHSampleColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("sampleIDssp"));
        EHExperimentalGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SexperimentalGeneration"));
        EHPickDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        EHBackupGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupGeneration"));
        EHBackupDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        EHNotesColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));

        EHTable.setItems(entriesClass.entryHistory);
    }

    //Component Events
        //TopBar
        public void exit() {
            System.exit(0);
        }

        //Entry Panel
        public void addEntryAction() {
            Entry entry;
            if(ExtinctButton.isSelected()==false){
                entry = new Entry(sampleNumberSpinner.getValue(), experimentalGenerationSpinner.getValue(), experimentalGenerationDatePicker.getValue(), backupGenerationSpinner.getValue(), backupOfDatePicker.getValue(), notesField.getText());
            }else {
                entry = new Entry(sampleNumberSpinner.getValue(), experimentalGenerationSpinner.getValue(), experimentalGenerationDatePicker.getValue(), -1, experimentalGenerationDatePicker.getValue(), notesField.getText());
            }
            if (!entriesClass.addEntry(entry)) {
                System.out.println("Could not add entry");
            }
        }

        public void deleteEntryButtonPressed(){
            Entry entry = (Entry) allEntriesTable.getSelectionModel().getSelectedItem();
            entriesClass.removeEntry(entry);
        }

        public void extinctButtonPressed(){
        if(ExtinctButton.isSelected()){
            backupGenerationSpinner.setDisable(true);
            backupOfDatePicker.setDisable(true);
        }else{
            backupGenerationSpinner.setDisable(false);
            backupOfDatePicker.setDisable(false);
        }
    }

        //Sample List Panel

        public void PrintList(){
            entriesClass.writeCalculatedEntriesCommaDelimited(SLGenerationSpinner.getValue());
        }

        public void PrintAdvancedList(){
            entriesClass.writeCalculatedEntriesWithBackupAndResetsCommaDelimited(SLGenerationSpinner.getValue());

        }

        public void PrintBackupTable(){
            entriesClass.writeBackupTable(SLGenerationSpinner.getValue());

        }

        public void PrintHistory(){
            entriesClass.writeBackupHistory(SLGenerationSpinner.getValue());
        }

        public void PrintEntriesCSV(){
            entriesClass.writeEntriesCSV();
        }

}
