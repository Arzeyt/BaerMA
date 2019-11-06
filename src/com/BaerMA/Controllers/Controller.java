package com.BaerMA.Controllers;

import com.BaerMA.*;
import com.BaerMA.DataObjects.CalculatedEntry;
import com.BaerMA.DataObjects.Entry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Initializable{


    //add entry panel
    @FXML private Spinner<Integer> experimentalGenerationSpinner;
    @FXML private DatePicker experimentalGenerationDatePicker;
    @FXML private Text experimentalGenerationDateText;
    @FXML private Spinner<Integer> sampleNumberSpinner;
    @FXML private CheckBox ExtinctButton;
    @FXML private Spinner<Integer> backupGenerationSpinner;
    @FXML private DatePicker backupOfDatePicker;
    @FXML private TextArea notesField;
    @FXML private Label AEInfoLabel;

    //all entries panel
    @FXML private TableView allEntriesTable;
    @FXML private TableColumn AELineColumn, AESampleColumn,AEExperimentalGenerationColumn,AEPickDateColumn,AEBackupGenerationColumn,AEBackupDateColumn,AENotesColumn, AEBackupNumberColumn;

    //sample list panel
    @FXML private ProgressIndicator SLPrintProgress;
    @FXML private Spinner<Integer> SLGenerationSpinner;
    @FXML private TableView sampleListTable;
    @FXML private TableColumn SLSampleColumn, SLBackupNumberColumn, SLGenerationColumn, SLBackupCountColumn, SLResetCountColumn;

    //entry history panel
    @FXML private Spinner<Integer> EHSampleNumberSpinner;
    @FXML private TableView EHTable;
    @FXML private TableColumn EHLineColumn, EHSampleColumn, EHExperimentalGenerationColumn, EHPickDateColumn, EHBackupGenerationColumn, EHBackupDateColumn,EHNotesColumn,EHBackupNumberColumn;

    private Entries entriesClass = MainStage.entries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeAddEntryComponents();
        initializeAllEntriesTable();
        initializeSampleListTable();
        initializeSampleHistoryTable();
        MainStage.controller=this;
    }

    //1. Initializers--------------------------
    //Assigns spinner value factories and uses the focusedProperty event listener to enable reading accurate inputs. This is a work-around to a bug
    private void initializeAddEntryComponents() {
        experimentalGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,999,0));
        experimentalGenerationSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                experimentalGenerationSpinner.increment(0  );
                updateExperimentalGenerationDateRecommendation();
            }
        });

        sampleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(500,1099,500));
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
        AELineColumn.setCellValueFactory(new PropertyValueFactory<Entry,String>("linessp"));
        AESampleColumn.setCellValueFactory(new PropertyValueFactory<Entry,Integer>("sampleIDssp"));
        AEExperimentalGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry,Integer>("SexperimentalGeneration"));
        AEPickDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        AEBackupGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SbackupGeneration"));
        AEBackupDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        AENotesColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));
        AEBackupNumberColumn.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SbackupNumber"));

        allEntriesTable.setItems(entriesClass.entriesList);
        allEntriesTable.getSortOrder().add(AEExperimentalGenerationColumn);
    }

    private void initializeSampleListTable() {
        SLSampleColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.sampleIDString));
        SLBackupNumberColumn.setCellValueFactory(new PropertyValueFactory<CalculatedEntry, String>(CalculatedEntry.backupNumberString));
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

        EHLineColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("linessp"));
        EHSampleColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("sampleIDssp"));
        EHExperimentalGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SexperimentalGeneration"));
        EHPickDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SpickDate"));
        EHBackupGenerationColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupGeneration"));
        EHBackupDateColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("SbackupOfDate"));
        EHNotesColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("Snotes"));
        EHBackupNumberColumn.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("SbackupNumber"));

        EHTable.setItems(entriesClass.entryHistory);
    }


    //2. Component Events----------------
        //a. TopBar---------
            public void exit() {
            System.exit(0);
        }

            public void loadEntriesFile_ButtonPressed(){
                Entries.entriesList.clear();
                Entries.entryHistory.clear();
                Entries.calculatedEntries.clear();
                FileDialog dialog = new FileDialog((Frame)null, "Select Entries File to Open");
                dialog.setMode(FileDialog.LOAD);
                dialog.setFile("*.json");
                dialog.setVisible(true);
                String directory = dialog.getDirectory();
                String fileName = dialog.getFile();

                File test = new File(directory+File.separator+fileName);
                System.out.println(test.getAbsolutePath());
                System.out.println(test.exists());
                System.out.println(test.canRead());
                System.out.println(test.isDirectory());

                Entries.parseEntriesJSON(test);
                Entries.calcBackups();
                sortByExperimentalGen();
            }

            public void ExportToCSV_ButtonPressed(){
                Entries.writeEntriesCSV();
            }

            public void openTerminal(){
                System.out.println("openingTerminal");
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("BaerTerminal.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void openPickers(){
                System.out.println("opening pickers");
                try{
                    Parent root = FXMLLoader.load(getClass().getResource("Pickers.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }catch (Exception e){
                    e.printStackTrace();
                    MainStage.alertError("Failed to Load Picker Interface","","");
                }
            }

            public void backupEntriesFileButtonPressed(){
                MainStage.entries.backupEntries();
            }

        //b. Entry Panel-------
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
                allEntriesTable.getSortOrder().add(AEExperimentalGenerationColumn);
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

            public void experimentButtonPressed(){
                //fileListRoot();
            }

        //c. Sample List Panel (Top right)------

            public void PrintBaerSheet(){ExcelMaster.createBaerSheet(SLGenerationSpinner.getValue(),true);}

            public void PrintBaerSheetUpTo(){ExcelMaster.createBaerSheetUpTo(SLGenerationSpinner.getValue());}

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

            public void PrintFormattedEntries(){entriesClass.printFormattedEntries(SLGenerationSpinner.getValue());}

            public void PrintAllFormattedEntries(){entriesClass.printAllFormattedEntries(SLGenerationSpinner.getValue());}

            public void PrintAllFormattedEntries_docx(){
                WordMaster.printFormattedEntries(SLGenerationSpinner.getValue());
            }

            public void PrintGenerationVisualizer(){
                ExcelMaster.createGenerationVisualizer(SLGenerationSpinner.getValue());
            }

            public void PrintGenerationStats(){
                MainStage.entries.printGenerationsBehindStats(SLGenerationSpinner.getValue());
            }

            public void PrintGenerationsBehindSequential(){
                MainStage.entries.printGenerationsBehindSequential(SLGenerationSpinner.getValue());
            }

            public void PrintGenerationsBehindSequentialExcel(){ExcelMaster.createAverageBackupGraphingSheet(SLGenerationSpinner.getValue());}

            public void PrintBackupsPerGenCSV(){
                MainStage.entries.printBackupsPerLinePerGen(SLGenerationSpinner.getValue());
            }


    //3. Miscellaneous Methods-------------
    public void updateExperimentalGenerationDateRecommendation(){
        LocalDate date = Entries.getDateForGeneration(experimentalGenerationSpinner.getValue());
        experimentalGenerationDateText.setText("Recommended: "+date.getMonthValue()+"/"+date.getDayOfMonth()+"/"+date.getYear());
    }
    public void sortByExperimentalGen(){
        allEntriesTable.getSortOrder().add(AEExperimentalGenerationColumn);
    }
    public void setSLPrintProgress(float progress){
        if(progress<=0F){
            System.out.println("progress <=");
            SLPrintProgress.setVisible(false);
        }else{
            System.out.println("showing progress");
            SLPrintProgress.setVisible(true);
            SLPrintProgress.setProgress(progress);
        }
    }
    public void setAEInfoLabel(String text){
        AEInfoLabel.setText(text);
    }
    public void fileListRoot() {

        ArrayList<Path> pngs = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(File.listRoots()[0].getPath()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }


                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if (exc instanceof AccessDeniedException) {
                        System.out.println("Access Denied at: "+file.toAbsolutePath()+"\nSkipping subtree");
                        return FileVisitResult.SKIP_SUBTREE;
                    }else if(exc instanceof NoSuchFileException){
                        System.out.println(file+" returned NoSuchFileException");
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return super.visitFileFailed(file, exc);
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if(attrs.isDirectory()==false) {
                        if(path.toString().contains(".png")) {
                            if(new File(path.toString()).length()>=(long)1000000*8) {
                                System.out.println("adding large png");
                                pngs.add(path);
                            }
                        }
                    }
                    return super.visitFile(path,attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(pngs.size()>0){

            for(Path p : pngs){
                //System.out.println(p);
            }
            System.out.println("PNGS found: "+pngs.size());
        }
    }


}
