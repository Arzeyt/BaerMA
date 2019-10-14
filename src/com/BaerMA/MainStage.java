package com.BaerMA;

import com.Controllers.Controller;
import com.Controllers.PickersController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainStage{

    //much of this is deprecated due to using SceneBuilding instead. Check the controller class to configure the GUI.

    Text generationText;
    Text dateText;


    //entry variables
    int sampleID, experimentalGeneration, backupGeneration;
    LocalDate experimentalDate;
    LocalDate backupOfDate;
    String notes;

    //entry fields
    Spinner<Integer> experimentalGenerationSpinner;
    DatePicker pickDatePicker;
    Spinner<Integer> sampleNumberSpinner;
    TextField sampleNumberTextField;
    Spinner<Integer> backupGenerationSpinner;
    TextField notesField;


    //class instances
    public static Entries entries;
    public static Controller controller;
    public static Settings settings;
    public static PickersController pickersController;


    //Entry Table Components
    TableView entryTable;

    //sample list components
    Spinner<Integer> sampleListGenerationSpinner;
    TableView sampleListTable;

    //
    TableView entryHistoryTable;

    public void start(Stage primaryStage) throws Exception {
        //It's important to keep the initialization of these static classes in order
        //settings
        settings=new Settings();
        settings.initialize();
        //entries
        entries =new Entries();
        entries.parseEntriesJSON(settings.entriesFile);
        //entriesClass.loadEntriesFromCSV();
        entries.calcBackups();

        //this initializes controller
        System.out.println("Resource: "+getClass().getResource("main.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("BaerMA");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        controller.sortByExperimentalGen();




    }

    public static void alertError(String title, String headerText, String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void alertInfo(String title, String headerText, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.showAndWait();
    }
    //---------deprecated code. It's all handled by javafx now.
    private Pane createSuperPane(){
        GridPane superPane = new GridPane();

        ColumnConstraints column1 = new ColumnConstraints();
        //column1.setMinWidth(sceneWidth*0.5);
        superPane.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        //column2.setMinWidth(sceneWidth*0.5);
        superPane.getColumnConstraints().add(column2);

        superPane.add(createEntrySettingsPane(),0,0);
        superPane.add(createEntryTable(),0,1);
        superPane.add(createSampleListPane(),1,0);
        superPane.add(createEntryHistoryPane(), 1, 1);

        superPane.setGridLinesVisible(true);
        return superPane;
    }

    private Pane createEntrySettingsPane() {

        GridPane entrySettingsPane = new GridPane();
        entrySettingsPane.setGridLinesVisible(false);
        entrySettingsPane.setPadding(new Insets(5, 5, 5, 5));
        entrySettingsPane.setHgap(5);
        entrySettingsPane.setVgap(5);

        entrySettingsPane.add(createEditSettingsLabel(),0,0);

        //entry experimental generation
        entrySettingsPane.add(new Label("Experimental Generation: "),0,1);
        entrySettingsPane.add(createGenerationText(),1,1);
        entrySettingsPane.add(createExperimentalGenerationSpinner(),2,1);

        //entry date
        entrySettingsPane.add(new Label("Date: "), 0, 2);
        entrySettingsPane.add(createPickDatePicker(),2, 2);

        //entry sample number
        entrySettingsPane.add(new Label("Sample Number"),0,3);
        entrySettingsPane.add(createSampleNumberSpinner(),2,3);

        //entry backup generation
        entrySettingsPane.add(new Label("Backup Generation"),0,4);
        entrySettingsPane.add(createBackupGenerationSpinner(),2,4);

        //entry backup "of" date
        entrySettingsPane.add(new Label("of"),0,5);
        entrySettingsPane.add(createBackupOfDatePicker(),2,5);

        //entry Notes field
        entrySettingsPane.add(new Label("Notes"),0,6);
        entrySettingsPane.add(createNotesField(),2,6);

        //add entry button
        entrySettingsPane.add(createAddEntryButton(),0,7);

        //clear all button
        //entrySettingsPane.add(createClearAllEntriesButton(),2,7);

        //delete entry button
        entrySettingsPane.add(createDeleteEntryButton(),3,7);

        //test function button
        entrySettingsPane.add(createTestFunctionButton(), 4, 7);

        return entrySettingsPane;
    }

    private TableView createEntryTable(){
        entryTable = new TableView();


        TableColumn sampleNumber = new TableColumn("Sample");
        TableColumn sampleGeneration = new TableColumn("Experimental Generation");
        TableColumn pickDate = new TableColumn("Pick Date");
        TableColumn backupGeneration = new TableColumn("Backup Generation");
        TableColumn backupOfDate = new TableColumn("Backup Of Date");
        TableColumn notes = new TableColumn("Notes");

        sampleNumber.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("sampleIDssp"));
        sampleGeneration.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SexperimentalGeneration"));
        pickDate.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        backupGeneration.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SbackupGeneration"));
        backupOfDate.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        notes.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));

        entryTable.getColumns().addAll(sampleNumber,sampleGeneration,pickDate,backupGeneration, backupOfDate, notes);
        entryTable.setItems(entries.entriesList);


        entryTable.setOnKeyPressed(event -> {
            //delete entries with del button
            if(event.getCode()== KeyCode.DELETE){
                entries.removeEntry((Entry) entryTable.getFocusModel().getFocusedItem());
            }
        });

        return entryTable;
    }

    private Pane createEntryHistoryPane(){
        GridPane pane = new GridPane();
        Label title = new Label("Entry History");
        pane.add(title,0,0);
        pane.add(createEntryHistoryTable(),0,1);
        return pane;
    }

    private TableView createEntryHistoryTable(){
        entryHistoryTable = new TableView();


        TableColumn sampleNumber = new TableColumn("Sample");
        TableColumn sampleGeneration = new TableColumn("Experimental Generation");
        TableColumn pickDate = new TableColumn("Pick Date");
        TableColumn backupGeneration = new TableColumn("Backup Generation");
        TableColumn backupOfDate = new TableColumn("Backup Of Date");
        TableColumn notes = new TableColumn("Notes");

        sampleNumber.setCellValueFactory(new PropertyValueFactory<Entry, String>("sampleIDssp"));
        sampleGeneration.setCellValueFactory(new PropertyValueFactory<Entry, String>("SexperimentalGeneration"));
        pickDate.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        backupGeneration.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupGeneration"));
        backupOfDate.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        notes.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));

        entryHistoryTable.getColumns().addAll(sampleNumber,sampleGeneration,pickDate,backupGeneration, backupOfDate, notes);
        entryHistoryTable.setItems(entries.entryHistory);

        return entryHistoryTable;
    }

    private Pane createSampleListPane(){
        GridPane pane = new GridPane();
        pane.setGridLinesVisible(true);
        pane.add(createSampleListGenerationSpinner(),0,0);
        pane.add(createSampleListPrintButton(),0,1);
        pane.add(createSampleListPrintButtonAdvanced(),1,1);
        pane.add(createBackupTablePrintButton(),1,2);
        pane.add(createSampleListTable(),0,2);

        return pane;
    }

    private Button createSampleListPrintButton(){
        Button button = new Button("Print List");
        button.setOnAction(event -> {
           // entriesClass.writeCalculatedEntriesCommaDelimited();
            System.out.println("Printed Calculated Entries, Comma delimited");
        });

        return button;
    }

    private Button createSampleListPrintButtonAdvanced(){
        Button button = new Button("Print Advanced List");
        button.setOnAction(event -> {
            //entriesClass.writeCalculatedEntriesWithBackupAndResetsCommaDelimited();
            System.out.println("Printed Calculated Entries with backup and reset number, Comma delimited");
        });

        return button;
    }

    private Button createBackupTablePrintButton(){
        Button button = new Button("Print Backup Table");
        button.setOnAction(event -> {
            entries.writeBackupTable(22);
            System.out.println("Printed BackupTable");
        });

        return button;
    }

    private TableView createSampleListTable(){
        sampleListTable = new TableView();

        TableColumn sampleNumber = new TableColumn("Sample");
        sampleNumber.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.sampleIDString));

        TableColumn backupNumber = new TableColumn("BK");
        backupNumber.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String >(CalculatedEntry.backupNumberString));

        TableColumn sampleGeneration = new TableColumn("Generation");
        sampleGeneration.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.calculatedGenerationString));

        TableColumn backupCount = new TableColumn("Backup Count");
        backupCount.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.backupCountString));

        TableColumn resetCount = new TableColumn("Reset Count");
        resetCount.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.resetCountString));

        sampleListTable.getColumns().addAll(sampleNumber,sampleGeneration,backupCount,resetCount);
        sampleListTable.setItems(entries.calculatedEntries);
        return sampleListTable;
    }

    private Button createDeleteEntryButton(){
        Button deleteEntryButton = new Button("Delete Entry");
        deleteEntryButton.setOnAction(event -> {
            Entry entry = (Entry) entryTable.getSelectionModel().getSelectedItem();
            entries.removeEntry(entry);
        });
        return deleteEntryButton;
    }


    private Spinner<Integer> createSampleListGenerationSpinner(){
        sampleListGenerationSpinner = new Spinner<Integer>(0,999,0);
        sampleListGenerationSpinner.setEditable(true);
        sampleListGenerationSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                entries.createCalculatedEntriesList(newValue);
                System.out.println("\nOld Value: "+oldValue+"\nNew Value: "+newValue);
            }
        });

        return sampleListGenerationSpinner;
    }

    private Button createClearAllEntriesButton(){
        Button button = new Button("Clear All");
        button.setOnAction(event -> {
            entries.clearEntries();
        });
        return button;
    }

    private Button createTestFunctionButton(){
        Button button = new Button("Experiment");
        button.setOnAction(event -> {
            /**
            Entry entry = (Entry)entryTable.getSelectionModel().getSelectedItem();
            CalculatedEntry calculatedEntry = new CalculatedEntry(entry.id, experimentalGenerationSpinner.getValue(),entriesClass.entriesList);
            System.out.println("calculated entry is "+calculatedEntry);
            entriesClass.calculatedEntries.add(calculatedEntry);
             */

            //entriesClass.createCalculatedEntriesList(experimentalGenerationSpinner.getValue());


        });
        return button;
    }
    private Button createAddEntryButton(){
        Button button = new Button("Add Entry");
        button.setOnAction(event -> {
            Entry entry = new Entry(sampleNumberSpinner.getValue(),experimentalGenerationSpinner.getValue(), pickDatePicker.getValue(),backupGenerationSpinner.getValue(),backupOfDate,notesField.getText());
            if(!entries.addEntry(entry)){
                System.out.println("Could not add entry");
            }
        });
        return button;
    }

    private TextField createNotesField(){
        notesField = new TextField();
        notesField.setOnAction(event -> {
            this.notes=notesField.getText();
        });
        return notesField;
    }
    private DatePicker createBackupOfDatePicker(){
        DatePicker picker = new DatePicker();
        picker.setOnAction(action -> {
            backupOfDate = picker.getValue();
            System.out.println("backup date is: "+backupOfDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))+" "+backupOfDate);
        });
        return picker;
    }

    private Spinner createSampleNumberSpinner(){
        sampleNumberSpinner = new Spinner<Integer>(500,999,500);
        sampleNumberSpinner.setEditable(true);
        //events aren't triggered...

        //hackish way to force update. When focus is lost, triggers an update by incrementing the value by 0
        sampleNumberSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                sampleNumberSpinner.increment(0); // won't change value, but will commit editor
                //display generation history
                entries.createEntryHistoryList(sampleNumberSpinner.getValue());
                System.out.println("Showing history for sample "+sampleNumberSpinner.getValue());
            }
        });

        return sampleNumberSpinner;
    }

    private Spinner createBackupGenerationSpinner(){
        backupGenerationSpinner = new Spinner<Integer>(0,500,1);
        backupGenerationSpinner.setEditable(true);
        backupGenerationSpinner.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue){
                backupGenerationSpinner.increment(0);
            }
        }));
        return backupGenerationSpinner;
    }

    private Label createEditSettingsLabel(){
        Label l = new Label("Create a new Entry");
        l.setFont(Font.font(20));
        l.setOnMouseClicked(new EventHandler<MouseEvent>() {
            int i = 0;
            @Override
            public void handle(MouseEvent event) {
                i++;
                if(i%2==0) {
                    l.setText("EDIT SETTINGS");
                }else{
                    l.setText("Edit Settings");
                }

            }
        });
        return l;
    }

    private Text createGenerationText(){
       generationText = new Text("");
       return generationText;
    }

    private Spinner<Integer> createExperimentalGenerationSpinner(){
        experimentalGenerationSpinner = new Spinner<Integer>(0,500,0);
        experimentalGenerationSpinner.setEditable(true);
        experimentalGenerationSpinner.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue){
                experimentalGenerationSpinner.increment(0  );
            }
        }));
        return experimentalGenerationSpinner;
    }

    private Text createDateText(){
        dateText=new Text("");
        return dateText;
    }
    private TextField createDateField(){
        LocalDate ld = LocalDate.now();
        TextField dateField = new TextField(DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH).format(ld));
        dateField.textProperty().addListener((observable,oldValue,newValue) -> {
            System.out.println("new value: "+oldValue+ " "+newValue);
        });
        return dateField;
    }

    private DatePicker createPickDatePicker(){
        pickDatePicker = new DatePicker();
        pickDatePicker.setOnAction(action -> {
            experimentalDate = pickDatePicker.getValue();
            System.out.println("active date is: "+ experimentalDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        });
        return pickDatePicker;
    }



}
