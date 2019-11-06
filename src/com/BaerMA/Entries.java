package com.BaerMA;

import com.BaerMA.DataObjects.CalculatedEntry;
import com.BaerMA.DataObjects.Entry;
import com.BaerMA.DataObjects.LineObject;
import com.BaerMA.Sorting.SortByDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Created by Nick on 1/31/2019.
 */
public class Entries {

    //variables-------------
    public static int numberOfInstaces = 0;

    public static final ObservableList<Entry> entriesList = FXCollections.observableArrayList();
    public static final ObservableList<CalculatedEntry> calculatedEntries = FXCollections.observableArrayList();
    public static final ObservableList<Entry> entryHistory = FXCollections.observableArrayList();

    static File dataFile = MainStage.settings.dataDirectory;
    static File outputFile = MainStage.settings.outputDirectory;

    //Constructor----------
    public Entries(){

        numberOfInstaces++;
        System.out.println("Entries instances: "+numberOfInstaces);

        System.out.print("Checking for data file: ");
        if (dataFile.exists()==false){
            dataFile.mkdir();
            System.out.println("Made data directory");
        }else{
            System.out.println("Found data file");
        }

        System.out.print("Checking for output file: ");
        //this should be done at each file write location
        if(outputFile.exists()==false){
            outputFile.mkdir();
            System.out.println("Made output directory");
        }else {
            System.out.println("Found output file");
        }
    }


    //add and remove Entries
    public boolean addEntry(Entry entry){
        //to avoid illegal entries, ensure that Entry.equals(Entry) returns true for any entry that matches the same experimental
        //generation date

        boolean oEntriesAdd = false;

        if(!entriesList.contains(entry)){
            entriesList.add(entry);
            oEntriesAdd=true;
            calcBackups();
            System.out.println("Added entry to entries list");
            MainStage.controller.setAEInfoLabel("Added Entry: "+entry.getSampleIDssp());
        }

        jsonizeEntries();
        return oEntriesAdd;

    }
    public void removeEntry(Entry e){

        if(entriesList.contains(e)){
            entriesList.remove(e);
            MainStage.controller.setAEInfoLabel("Removed entry: "+e.getSampleIDssp());
        }

        jsonizeEntries();
    }

    //Data Storage----------------
    //JSON
    public void backupEntries(){
        jsonizeEntries();

        //Date builder
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateString = localDate.format(formatter);

        //TimeBuilder
        LocalTime localTime = LocalTime.now();
        String timeString = localTime.format(DateTimeFormatter.ofPattern("HHmmss"));


        String backupPath = MainStage.settings.dataDirectory+"Entries"+dateString+timeString+".json");
        File file = new File(backupPath);
        try {
            FileOutputStream os = new FileOutputStream(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void jsonizeEntries(){
        ArrayList<Entry> entries = new ArrayList<>();
        for(Entry e : entriesList){
            entries.add(e);
        }

        GsonBuilder gsonBuilder= new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson=gsonBuilder.create();

        try {
            FileWriter writer = new FileWriter(MainStage.settings.dataDirectory +File.separator+"Entries.json");
            writer.write(gson.toJson(entries));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void parseEntriesJSON(File file){

        try {

            FileReader reader = new FileReader(file);
            Gson gson=new Gson();

            ArrayList<Entry> entries = gson.fromJson(reader,new TypeToken<ArrayList<Entry>>(){}.getType());

            reader.close();
            Main.dividingFlair();
            //none of these deserialized entries are initialized properly, which is currently required to create StringProperty objects
            // within the Entry that enables JavaFX tables to populate the list.
            // The solution is to create new object based on the JSON data, and place these new Entry objects in the entries list
            for(Entry e : entries){
                Entry iEntry = new Entry(e.id,e.experimentalGeneration,e.pickDate,e.backupGeneration,e.backupOfDate,e.notes);
                entriesList.add(iEntry);
            }
            Main.dividingFlair();
            System.out.println("Loaded "+entriesList.size()+" entries");
        } catch (FileNotFoundException e) {
           e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadEntriesFromCSV(){
        try {
            File csv = new File(dataFile+File.separator+"Entries.csv");
            //System.out.println("absolute path: "+csv.getAbsolutePath()+" cannonnical path: "+csv.getCanonicalFile());
            Scanner scanner = new Scanner(csv);
            scanner.useDelimiter("\\Z");

            String string = scanner.next();
            String[] byLine = string.split("\n");
            System.out.println("Loading "+byLine.length+" entries from CSV");


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("CSV Entries file does not exist");
        }
    }

    //CSV
    public static void writeEntriesCSV(){
        try{
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile+File.separator+"Entries.csv"),"utf-8"));

            String header = "Sample ID,Pick Date,Experimental Generation,Backup Generation,Backup Date,Notes\n";
            writer.write(header);
            //loop through all entries and write to CSV
            int entriesCounter = 0;

            for(Entry e : entriesList){
                entriesCounter++;
                int id = e.sampleIDssp.getValue();
                String pickDate = e.SpickDate.getValue();
                int expGen = e.SexperimentalGeneration.getValue();
                int backupGen = e.SbackupGeneration.getValue();
                String backupDate = e.SbackupOfDate.getValue();
                String notes = e.Snotes.getValue();

                writer.write(id+","+pickDate+","+expGen+","+backupGen+","+backupDate+","+notes+"\n");

            }
            System.out.println("Wrote "+entriesCounter+" entries to Entries.csv");
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //serialization (deprecated)
        public void serializeEntries(){
        try
        {
            File saveFile = new File("entries");
            String filePath=saveFile.getAbsolutePath();

            //Saving of object in a file
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);

            // Method for serialization of object
           ArrayList<Entry> array = new ArrayList<>();
           for(Entry e : entriesList){
               array.add(e);
           }
           outputStream.writeObject(array);

            outputStream.close();
            fileOutputStream.close();


            System.out.println("entries have been serialized");

        }

        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
        public void deserializeEntries(){
        if(new File("entries").exists()){
            System.out.println("Entry file found");
        }else{
            System.out.println("Entries does not exist");
            return;
        }
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream("entries");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            ArrayList<Entry> array = (ArrayList<Entry>) in.readObject();

            for(Entry entry : array){
                Entry entry1 = new Entry(entry.id,entry.experimentalGeneration,entry.pickDate,entry.backupGeneration,entry.backupOfDate,entry.notes);
                entriesList.add(entry1);
            }

            in.close();
            file.close();

            System.out.println(array.size()+" entries have been deserialized ");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    //list management functions
        public void clearEntries(){
        entriesList.clear();
        jsonizeEntries();
        System.out.println("Entries cleared");
    }

//Calculated Data----------------------

    //calculated entries
    public static void createCalculatedEntriesList(int experimentalGeneration){
        calculatedEntries.clear();
        for(int i =500; i<=1099; i++){
            CalculatedEntry calculatedEntry = new CalculatedEntry(i,experimentalGeneration, entriesList);
            calculatedEntries.add(calculatedEntry);
            /**System.out.println("Calculated Entry id: "+calculatedEntry.sampleID.get()+" simpleStringProperty ID: "+calculatedEntry.sampleIDProperty()
            +" calculatedGeneration: "+calculatedEntry.calculatedGeneration+" "+calculatedEntry.calculatedGeneration.get());
             */
        }

        System.out.println("Number of entries in calculated entry: "+calculatedEntries.size());
    }

    public boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static void calcBackups(){
        //SampleNumber, list containing all entries for that sample number
        HashMap<Integer,ArrayList<Entry>> entriesBySample = new HashMap<>();

        int debug=0;

        //sampleID by experimentalGen by sampleGen
        for(Entry e : entriesList){
            if(debug==0) {
                //debug sout
            }

            //if there is an entryList for this sample number, add this new entry to that existing list
            if(entriesBySample.get(e.id)!=null) {
                entriesBySample.get(e.id).add(e);
                //else, create a new entryList array and add this entry to that list, and add that array to the entriesBySample array under the sample id index
            }else{
                ArrayList<Entry> entryList = new ArrayList<Entry>();
                entryList.add(e);
                entriesBySample.put(e.id,entryList);
            }
        }

        //sort the array containing the entries for each sample number by experimental generation
        for(ArrayList<Entry> list : entriesBySample.values()){
            Collections.sort(list, new Comparator<Entry>() {
                @Override
                public int compare(Entry o1, Entry o2) {
                    return o1.experimentalGeneration-o2.experimentalGeneration;
                }
            });
        }

        Iterator i = entriesBySample.keySet().iterator();
        int currentId=0;
        int bk=0;

        //for each sample id...
        while(i.hasNext()){
            int lastGen = 0;
            int id = (int)i.next();
            //loop through the array value and compare expgen to previous expgen
            for(Entry e : entriesBySample.get(id)){
                if (id == 618){
                    System.out.println("618: "+e.experimentalGeneration+" "+lastGen);
                }
                if(e.experimentalGeneration-lastGen==1){
                    bk++;
                }else{
                    bk = 1;
                }
                lastGen=e.experimentalGeneration;
                e.setBackupNumber(bk);
                e.SbackupNumber=new SimpleIntegerProperty(bk);
                if(debug==1)System.out.println("id: "+id+" bk: "+bk+" expgen: "+e.experimentalGeneration);
            }
        }

        //place back into observable list
        entriesList.clear();
        for(ArrayList<Entry> list : entriesBySample.values()){
            for(Entry e : list){
                entriesList.add(e);
            }
        }
    }

    public static ArrayList<Entry> getEntriesForSampleNumber(int sampleNumber){
        ArrayList<Entry> entries = new ArrayList<>();
        for(Entry e : entriesList){
            if(e.id==sampleNumber) {
                entries.add(e);
            }
        }
        if(entries.size()<1){
            return null;
        }else{
            return entries;
        }
    }

    public ArrayList<Entry> getEntriesForGeneration(int generation){
        ArrayList<Entry> list = new ArrayList<>();
        for(Entry e : entriesList){
            if(e.experimentalGeneration==generation){
                list.add(e);
            }
        }
        return list;
    }


    //Writing Functions--------------------------
    //write sample id and generation for samples 500 to 999.
    //This function relies on the pre-calculation of the calculatedEntries list and should be modified to allow ToF calculation.
    public void writeCalculatedEntriesCommaDelimited(int generation){

        try
        {
            File file = new File(outputFile+File.separator+"Generation_of_all_samples_by_MA_generation_"+generation+".csv");
            if(outputFile.exists()==false)outputFile.mkdir();

            //Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("calculated generation list "+generation+".csv"),"utf-8"));
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            System.out.println("filename is: "+file);

            //print header
            writer.write("Sample ID,Generation\n");
            for(CalculatedEntry e : calculatedEntries){
                String calculatedGeneration;
                if(e.calculatedGenerationProperty().getValue()==-1){
                    calculatedGeneration="Extinct";
                }else{
                    calculatedGeneration=e.calculatedGenerationProperty().getValue()+"";
                }
                String line = e.sampleIDProperty().getValue()+","+calculatedGeneration+"\n";
                //System.out.println(line);
                writer.write(line);
            }
            writer.close();
            Desktop.getDesktop().open(file);

        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            MainStage.alertError("Write error","",ex.getLocalizedMessage());
        }
    }

    //write sample id, generation, backups, resets for samples 500 to 999
    public void writeCalculatedEntriesWithBackupAndResetsCommaDelimited(int generation){

        try
        {
            File file = new File(outputFile+File.separator+"Samples to Generation "+generation+".csv");
            if(outputFile.exists()==false)outputFile.mkdir();

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            //print header
            writer.write("Sample ID,Generation,Backup Count,Reset Count\n");
            for(CalculatedEntry e : calculatedEntries){

                ArrayList<Entry> sampleEntries = getEntriesForSampleNumber(e.sampleID.getValue());
                int backupCounter = 0;
                int resetCounter = 0;
                if (sampleEntries!=null) {
                    for (Entry entry : sampleEntries) {
                        if (entry.backupGeneration == 0 && entry.experimentalGeneration>=3) {
                            resetCounter++;
                        }else{
                            backupCounter++;
                        }
                    }
                }
                String calculatedGeneration;
                if(e.calculatedGenerationProperty().getValue()==-1){
                    calculatedGeneration="Extinct";
                }else{
                    calculatedGeneration=e.calculatedGenerationProperty().getValue()+"";
                }
                String line = e.sampleIDProperty().getValue()+","+calculatedGeneration+","+backupCounter+","+resetCounter+"\n";
                //System.out.println(line);
                writer.write(line);
            }

            writer.close();
            Desktop.getDesktop().open(file);

        }

        catch(IOException ex)
        {
            ex.printStackTrace();
            MainStage.alertError("Write error","",ex.getLocalizedMessage());
        }
    }

    public void writeBackupTable(int numberOfGenerations){

        try
        {
            File file = new File(outputFile+File.separator+"All Backups to generation "+numberOfGenerations+".csv");
            if(outputFile.exists()==false)outputFile.mkdir();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            //print header
            String header = "Sample ID";
            for(int i=1;i<=numberOfGenerations;i++){
                header=header+","+i;
            }
            header=header+"\n";
            writer.write(header);
            //for each sample number
            for(int csample=500;csample<=1099;csample++){

                ArrayList<Entry> sampleEntries = getEntriesForSampleNumber(csample);
                String line = csample+"";
                //loop through generation 1 through numberOfGenerations and append a 0 for no backup or 1 for backup for each generation
                for(int cgen=1;cgen<=numberOfGenerations;cgen++){
                    //check to see if entry for this sample
                    int backup = 0;
                    if(sampleEntries!=null){
                        for(Entry e : sampleEntries){
                            if(e.experimentalGeneration==cgen){
                                if(e.notes.contains("reset")){
                                    backup=2;
                                }else if(e.notes.contains("extinct")){
                                    backup=3;
                                }else{
                                    backup=1;
                                }
                            }
                        }
                    }
                    line=line+","+backup;
                }
                //System.out.println(line);
                writer.write(line+"\n");
            }

            writer.close();
            Desktop.getDesktop().open(file);
        }

        catch(IOException ex)
        {
            ex.printStackTrace();
            MainStage.alertError("Write error","",ex.getLocalizedMessage());
        }

    }

    //write sample id and sampleGeneration for each ExperimentalGeneration passed in
    public void writeBackupHistory(int numberOfGenerations){

        try
        {
            File file = new File(outputFile+File.separator+"Sample history to generation "+numberOfGenerations+".csv");
            if(outputFile.exists()==false)outputFile.mkdir();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            //print header
            String header = "Sample ID";
            for(int i=1;i<=numberOfGenerations;i++){
                header=header+","+i;
            }
            header=header+"\n";
            writer.write(header);

            //for each sample number
            for(int csample=500;csample<=999;csample++){

                ArrayList<Entry> sampleEntries = getEntriesForSampleNumber(csample);

                String line = csample+"";//build this line by appending sample generation for each experimental generations

                //if there are entries for this sample number
                if(sampleEntries!=null){
                    //for every experimental generation (1 through numberOfGenerations), append the generation of the sample to the line
                    for(int cgen=1; cgen<=numberOfGenerations;cgen++) {
                        line=line+","+CalculatedEntry.calculateGeneration(csample,cgen);
                    }
                }else{
                    for(int cgen=1; cgen<=numberOfGenerations;cgen++){
                        line=line+","+cgen;
                    }
                }



                //System.out.println(line);
                writer.write(line+"\n");
            }

            writer.close();
            Desktop.getDesktop().open(file);
        }catch(IOException ex) {
            ex.printStackTrace();
            MainStage.alertError("Write error","",ex.getLocalizedMessage());
        }
    }

    //write the formatted entry list according to Dr. Katju's guidelines
    public void printFormattedEntries(int experimentalGen){
        calcBackups();
        try{
            File file = new File(outputFile+File.separator+"Formatted Entries Gen "+experimentalGen+".txt");
            if(outputFile.exists()==false)outputFile.mkdir();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            ArrayList<Entry> entries = getEntriesForGeneration(experimentalGen);

            //divide the entries list into extinct and non extinct
            ArrayList<Entry> nonextinct = new ArrayList<>();
            ArrayList<Entry> extinct = new ArrayList<>();
            for(Entry e : entries){
                if(e.backupGeneration==-1){
                    extinct.add(e);
                }else{
                    nonextinct.add(e);
                }
            }

            //header
            writer.write(MainStage.settings.getFormattedEntriesListHeader(experimentalGen));

            //if there are entries in the nonextinct list, print them
            if(nonextinct!=null) {
                for (Entry e : nonextinct) {
                    String date = e.backupOfDate.getMonthValue() + "/" + e.backupOfDate.getDayOfMonth() + "/" + e.backupOfDate.getYear();
                    String line = null;
                    line = "BK" + e.backupNumbersp().getValue() + " " + e.getLineLetter() + e.id + "." + (CalculatedEntry.calculateGeneration(e.id, experimentalGen - 1)) + " from " + e.getLineLetter() + e.id + "." + e.backupGeneration + " of " + date + " " + e.notes
                            + "\r\n";
                    writer.write(line);
                }
            }
            //if there are entires in the extinct list, print them
            if(extinct!=null) {
                for (Entry e : extinct) {
                    writer.write(e.getLineLetter() + e.id + " extinct " + e.notes + "\r\n");
                }
            }
            writer.close();
            Desktop.getDesktop().open(file);
        }catch (Exception e){
            e.printStackTrace();
            MainStage.alertError("Write error","",e.getLocalizedMessage());
        }
    }

    //write all formatted entry lists up to the given gen
    public void printAllFormattedEntries(int experimentalGen){
        calcBackups();
        try{
            File file = new File(outputFile+File.separator+"All Formatted Entries to Gen "+experimentalGen+".txt");
            if(outputFile.exists()==false)outputFile.mkdir();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            for(int i=0; i<=experimentalGen; i++) {
                //header for each generation
                String header = MainStage.settings.getFormattedEntriesListHeader(i);
                String formattedHeader = "";
                String[] firstSplit = header.split("<");
                for(String s : firstSplit){
                    if(s.contains(">")) {
                        formattedHeader = formattedHeader + s.substring(s.lastIndexOf(">")+1, s.length());
                        System.out.println(formattedHeader);
                    }else{
                        formattedHeader=formattedHeader+s;
                    }
                }
                writer.write(formattedHeader+"\r\n");


                //writer.write("Generation: "+i+"\r\n");

                ArrayList<Entry> entries = getEntriesForGeneration(i);

                //divide the entries list into extinct and non extinct
                ArrayList<Entry> nonextinct = new ArrayList<>();
                ArrayList<Entry> extinct = new ArrayList<>();
                for(Entry e : entries){
                    if(e.backupGeneration==-1){
                        extinct.add(e);
                    }else{
                        nonextinct.add(e);
                    }
                }
                //if there are entries in the nonextinct list, print them
                if(nonextinct!=null) {
                    for (Entry e : nonextinct) {
                        String date = e.backupOfDate.getMonthValue() + "/" + e.backupOfDate.getDayOfMonth() + "/" + e.backupOfDate.getYear();
                        String line = null;
                        line = "BK" + e.backupNumbersp().getValue() + " " + e.getLineLetter() + e.id + "." + (CalculatedEntry.calculateGeneration(e.id, i - 1)) + " from " + e.getLineLetter() + e.id + "." + e.backupGeneration + " of " + date + " " + e.notes
                                + "\r\n";
                        writer.write(line);
                    }
                }
                //if there are entires in the extinct list, print them
                if(extinct!=null) {
                    for (Entry e : extinct) {
                        writer.write(e.getLineLetter() + e.id + " extinct " + e.notes + "\r\n");
                    }
                }
                //new line between gens
                writer.write("\r\n");
            }
            writer.close();
            Desktop.getDesktop().open(file);
        }catch (Exception e){
            e.printStackTrace();
            MainStage.alertError("Write error","",e.getLocalizedMessage());
        }
    }

    /**
     * @implNote prints .csv with the average generations behind for each generation and each line up to the
     * experimental generation provided
     * @param experimentalGeneration
     */
    public void printGenerationsBehindSequential(int experimentalGeneration){
        Thread thread = new Thread() {
            public void run() {
                try {
                    File file = new File(MainStage.settings.outputDirectory + File.separator + "Average Generations Behind to Gen " + experimentalGeneration + ".csv");
                    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

                    //header
                    writer.write(",Generation\n");
                    writer.write("Line,");
                    for (int i = 0; i <= experimentalGeneration; i++) {
                        writer.write(i + ",");
                    }
                    writer.write("\n");


                    //Line
                    ArrayList<LineObject> lines = MainStage.settings.lines;
                    int progressDataLength = lines.size() * experimentalGeneration;
                    int counter = 0;

                    //for each line
                    for (LineObject line : lines) {
                        //iterate through each generation and output average generations behind
                        for (int i = -1; i <= experimentalGeneration; i++) {
                            counter++;
                            float progress = (float) counter/progressDataLength;
                            MainStage.controller.setSLPrintProgress(progress);
                            System.out.println("progress: "+progress);
                            //line name
                            if (i == -1) {
                                writer.write(line.lineName + ",");
                            } else {
                                int start = line.lineStartNumber;
                                int end = line.lineEndNumber;
                                double[] stats = generationsBehindStats(start, end, i, false);
                                writer.write(stats[0] + ",");
                            }
                        }
                        writer.write("\n");
                    }
                    writer.write("\n");
                    writer.close();
                    Desktop.getDesktop().open(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * @implNote  write .csv file with the mean, stdev, and median of all lines for the given experimental generation
     * @param experimentalGeneration
     */
    public void printGenerationsBehindStats(int experimentalGeneration){
        try {
            File file = new File(MainStage.settings.outputDirectory + File.separator + "Stats for Gen " + experimentalGeneration + ".csv");
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

            //header
            writer.write(",Generations Behind\n");
            writer.write("Line,Mean,StdDev,Median\n");

            //Line
            ArrayList<LineObject> lines = MainStage.settings.lines;
            for(LineObject line : lines){
                String lineName = line.lineName;
                int start = line.lineStartNumber;
                int end = line.lineEndNumber;
                double[] stats = generationsBehindStats(start,end,experimentalGeneration,false);
                writer.write(lineName+","+stats[0]+","+stats[1]+","+stats[2]+"\n");
            }

            writer.close();
            Desktop.getDesktop().open(file);
        }catch (Exception e){
            e.printStackTrace();
            MainStage.alertError("Write error","",e.getLocalizedMessage());
        }

    }

    /**
     * @implNote Creates a file displaying the sample number and sample generation up to the experimental generation that was input
     * @param numberOfGenerations
     */
    public void writeBackupTableDetailed(int numberOfGenerations){

        try
        {

            File file = new File(MainStage.settings.outputDirectory+File.separator+"Sample Backups.csv");
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            //print header
            String header = "Sample ID";
            for(int i=1;i<=numberOfGenerations;i++){
                header=header+","+i;
            }
            header=header+"\n";
            writer.write(header);
            //for each sample number
            for(int csample=500;csample<=999;csample++){

                ArrayList<Entry> sampleEntries = getEntriesForSampleNumber(csample);
                String line = csample+"";
                //loop through generation 1 through numberOfGenerations and append a 0 for no backup or 1 for backup for each generation
                for(int cgen=1;cgen<=numberOfGenerations;cgen++){
                    //check to see if entry for this sample
                    int backup = 0;
                    if(sampleEntries!=null){
                        for(Entry e : sampleEntries){
                            if(e.experimentalGeneration==cgen){
                                if(e.notes.contains("reset")){
                                    backup=2;
                                }else if(e.notes.contains("extinct")){
                                    backup=3;
                                }else{
                                    backup=1;
                                }
                            }
                        }
                    }
                    line=line+","+backup;
                }
                //System.out.println(line);
                writer.write(line+"\n");
            }

            writer.close();

        }

        catch(IOException ex)
        {
            ex.printStackTrace();
            MainStage.alertError("Write error","",ex.getLocalizedMessage());
        }

    }

    public void printBackupsPerLinePerGen(int experimentalGeneration){
        try {
            File file = new File(MainStage.settings.outputDirectory + File.separator + "Backups per line per gen up to gen " + experimentalGeneration + ".csv");
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));

            //create backups per gen array
            ArrayList<Integer> backupsPerGen = new ArrayList<>();
            for(int i=0;i<=experimentalGeneration;i++){
                backupsPerGen.add(i,getEntriesForGeneration(i).size());
            }

            //header
            writer.write(",Generation\n");
            writer.write("Line,");
            for(int i=0;i<=experimentalGeneration;i++){
                writer.write(i+",");
            }
            writer.write("\n");

            //data
            //for each line, write number of backups
            for(LineObject line : MainStage.settings.lines){
                writer.write(line.lineName+",");
                for(int i=0;i<=experimentalGeneration;i++){
                    ArrayList<Entry> list = getEntriesForGeneration(i);
                    list.removeIf(e -> e.getSampleIDssp()<line.lineStartNumber || e.getSampleIDssp()>=line.lineEndNumber);
                    writer.write(list.size()+",");
                }
                writer.write("\n");
            }

            writer.close();
            Desktop.getDesktop().open(file);
        }catch (Exception e){
            e.printStackTrace();
            MainStage.alertError("Write error","",e.getLocalizedMessage());
        }
    }


//miscellaneous methods------------------------

    public static LocalDate getDateForGeneration(int experimentalGeneration){
        LocalDate date = LocalDate.of(2018,11,26);
        return date.plusDays(experimentalGeneration*4);
    }

    public void createEntryHistoryList(int sampleID){
        entryHistory.clear();
        ArrayList<Entry> entries = getEntriesForSampleNumber(sampleID);
        if(entries==null)return;
        Collections.sort(entries,new SortByDate());
        for(Entry e : entries){
            this.entryHistory.add(e);
        }
    }

//stats methods--------------------------

    /**
     *
     * @param fromSampleID
     * @param toSampleID
     * @param experimentalGeneration
     * @param includeExtinctions
     * @return double[0] = mean ; double[1] = stdev ; double[2] = median.
     * All values return -1 if there were no samples to run the stats on in the given sampleID range.
     */
    public double[] generationsBehindStats(int fromSampleID, int toSampleID, int experimentalGeneration, boolean includeExtinctions){
        System.out.println("----Sample "+fromSampleID+" to "+toSampleID+" ---------");

        List<Double> gensBehindList = new ArrayList<Double>();

        //create gensBehindList
        for(int i=fromSampleID;i<=toSampleID;i++){
            double gensBehind = getGenerationsBehind(i,experimentalGeneration,includeExtinctions);
            //System.out.println("calc: "+i+" gens behind: "+gensBehind);
            if(includeExtinctions){
                //this needs work
                gensBehindList.add((double) getGenerationsBehind(i, experimentalGeneration, includeExtinctions));
            }else {
                if (gensBehind != -1 && gensBehind!=-2) {
                    gensBehindList.add((double) getGenerationsBehind(i, experimentalGeneration, includeExtinctions));
                }
            }

        }

        System.out.println("ExpGen: "+experimentalGeneration);
        if(gensBehindList.size()>0){
            //convert from List to double[]
            double[] doubles = new double[gensBehindList.size()];
            for(int i =0;i<doubles.length;i++){
                doubles[i]=gensBehindList.get(i);
                //System.out.println("new calc: "+i+" gens behind: "+gensBehindList.get(i));
            }
            System.out.println("doubles[] size is: "+doubles.length);

            //calc average gens behind
            double mean = new Mean().evaluate(doubles);
            System.out.println("mean is:" +mean);

            //calc stdev gens behind
            double standardDeviation = new StandardDeviation().evaluate(doubles);
            System.out.println("Stdev is:" +standardDeviation);

            //calc median gens behind
            double median = new Median().evaluate(doubles);
            System.out.println("Median is: "+median);

            double[] stats = new double[3];
            stats[0]=mean;
            stats[1]=standardDeviation;
            stats[2]=median;

            return stats;
        }else{
            return new double[]{-1,-1,-1};
        }

    }


    /**
     *
     * @param sampleID
     * @param experimentalGeneration
     * @param includeExtinctions
     * @return returns -1 if the sample was extinct. TODO: this should return the generations behind for extinct
     * samples too by calculating the last gen before the sample went extinct and subtracting the experimentalGeneration
     * from that.
     */
    public int getGenerationsBehind(int sampleID, int experimentalGeneration, boolean includeExtinctions){
        int gensBehind = 0;

        CalculatedEntry e = new CalculatedEntry(sampleID,experimentalGeneration,entriesList);
        if(e.getcalculatedGeneration()==-2) {//does not exist
            gensBehind=0;
        }else{
            if(e.getcalculatedGeneration()==-1){//extinct
                if(includeExtinctions){
                    gensBehind=-1;
                }else{
                    gensBehind=-1;
                }
            }else{
                if(e.sampleID.getValue()>=1000 && e.sampleID.getValue()<=1099){ //J2 line
                    gensBehind=experimentalGeneration-MainStage.settings.J2LineGenesisGeneration-e.getcalculatedGeneration();
                }else {
                    gensBehind = experimentalGeneration - e.getcalculatedGeneration();
                }
            }
        }

        return gensBehind;
    }

    /**
     *
     * @param sampleNumber
     * @param experimentalGeneration
     * @return returns the largest valid entry before the given experimental generation
     */
    public Entry getLargestValidEntry(int sampleNumber, int experimentalGeneration){

        ArrayList<Entry> entryList = Entries.getEntriesForSampleNumber(sampleNumber);

        System.out.println("glve entryList size: "+entryList.size());

        if(entryList.size()<1){
            return null;
        }
        //ensure that entries with experimental generations larger than the input are ignored, but the largest one before that is picked
        entryList.sort(new SortByDate());
        Entry largestValidEntry = entryList.get(0); //start with the entry with an experimental generation sorted at the bottom.

        for(int i = 0; i<entryList.size(); i++){
            Entry e = entryList.get(i);
            if(e.experimentalGeneration<=experimentalGeneration){
                largestValidEntry=e;
            }else{
                break;
            }
        }
        return largestValidEntry;
    }
}
