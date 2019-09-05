package com.BaerMA;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by Nick on 1/31/2019.
 */
public class Entries {

    public static int numberOfInstaces = 0;

    public static final ObservableList<Entry> entriesList = FXCollections.observableArrayList();
    public static final ObservableList<CalculatedEntry> calculatedEntries = FXCollections.observableArrayList();
    public static final ObservableList<Entry> entryHistory = FXCollections.observableArrayList();

    File dataFile = new File("Data");
    static File outputFile = new File("Output");

    public Entries(){
        numberOfInstaces++;
        System.out.println("Entries instances: "+numberOfInstaces);

        System.out.println("Checking for data file");
        if (dataFile.exists()==false){
            dataFile.mkdir();
            System.out.println("made data directory");
        }
        System.out.println("Checking for output file");
        //this should be done at each file write location
        if(outputFile.exists()==false){
            outputFile.mkdir();
            System.out.println("made output directory");
        }
    }

    //add and remove Entries
        public boolean addEntry(Entry entry){
            //to avoid illegal entries, ensure that Entry.equals(Entry) returns true for any entry that matches the same experimental
            //generation date

            boolean oEntriesAdd = false;

            if(!entriesList.contains(entry)){
                System.out.println("Added entry to entries");
                entriesList.add(entry);
                oEntriesAdd=true;
                calcBackups();
            }

            jsonizeEntries();
            return oEntriesAdd;

        }
        public void removeEntry(Entry e){

        if(entriesList.contains(e)){
            entriesList.remove(e);
        }

        jsonizeEntries();
    }
//Data Storage
    //JSON
    public void jsonizeEntries(){
        ArrayList<Entry> entries = new ArrayList<>();
        for(Entry e : entriesList){
            entries.add(e);
        }

        GsonBuilder gsonBuilder= new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson=gsonBuilder.create();

        try {
            FileWriter writer = new FileWriter(dataFile+File.separator+"Entries.json");
            writer.write(gson.toJson(entries));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void parseEntriesJSON(){

        try {

            FileReader reader = new FileReader(dataFile+File.separator+"Entries.json");
            Gson gson=new Gson();

            ArrayList<Entry> entries = gson.fromJson(reader,new TypeToken<ArrayList<Entry>>(){}.getType());

            reader.close();
            Main.dividingFlair();
            //none of these deserialized entries are initialized properly, which is currently required to create StringProperty objects
            // within the Entry that enables JavaFX tables to populate the list.
            // The solution is to create new object based on the JSON data, and place these new Entry objets in the entries list
            //This calls into question the utility of the UUID system. I should deprecate that.
            for(Entry e : entries){
                Entry iEntry = new Entry(e.id,e.experimentalGeneration,e.pickDate,e.backupGeneration,e.backupOfDate,e.notes);
                entriesList.add(iEntry);
            }
            calcBackups();
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
    public void writeEntriesCSV(){
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
        for(int i =500; i<1000; i++){
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

    public ArrayList<Entry> getEntriesForGeneration(int generation){
        ArrayList<Entry> list = new ArrayList<>();
        for(Entry e : entriesList){
            if(e.experimentalGeneration==generation){
                list.add(e);
            }
        }
        return list;
    }

    public void calcBackups(){
        //SampleNumber, list containing all entries for that sample number
        HashMap<Integer,ArrayList<Entry>> entriesBySample = new HashMap<>();

        int debug=1;

        //sampleID by experimentalGen by sampleGen
        for(Entry e : entriesList){
            if(debug==1) {
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
        int lastGen = 0;
        int bk=0;

        //for each sample id...
        while(i.hasNext()){
            int id = (int)i.next();
            //loop through the array value and compare expgen to previous expgen
            for(Entry e : entriesBySample.get(id)){
                if(e.experimentalGeneration-lastGen==1){
                    bk++;
                }else{
                    bk = 1;
                }
                lastGen=e.experimentalGeneration;
                e.setBackupNumber(bk);
                e.SbackupNumber=new SimpleIntegerProperty(bk);
                System.out.println("id: "+id+" bk: "+bk+" expgen: "+e.experimentalGeneration);
            }
        }

        //place back into observable list
        entriesList.clear();
        for(ArrayList<Entry> list : entriesBySample.values()){
            System.out.println(" ");
            for(Entry e : list){
                System.out.print(e.id+","+e.backupNumbersp().getValue()+" ");
                entriesList.add(e);
            }
        }
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

                ArrayList<Entry> sampleEntries = CalculatedEntry.getEntriesForSampleNumber(e.sampleID.getValue(), entriesList);
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
            for(int csample=500;csample<=999;csample++){

                ArrayList<Entry> sampleEntries = CalculatedEntry.getEntriesForSampleNumber(csample, this.entriesList);
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

                ArrayList<Entry> sampleEntries = CalculatedEntry.getEntriesForSampleNumber(csample, this.entriesList);

                String line = csample+"";//build this line by appending sample generation for each experimental generations

                //if there are entries for this sample number
                if(sampleEntries!=null){
                    //for every experimental generation (1 through numberOfGenerations), append the generation of the sample to the line
                    for(int cgen=1; cgen<=numberOfGenerations;cgen++) {
                        line=line+","+CalculatedEntry.calculateGeneration(csample,cgen, entriesList);
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
        }

        catch(IOException ex)
        {
            ex.printStackTrace();
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
            //if there are entries in the nonextinct list, print them
            if(nonextinct!=null) {
                for (Entry e : nonextinct) {
                    String date = e.backupOfDate.getMonthValue() + "/" + e.backupOfDate.getDayOfMonth() + "/" + e.backupOfDate.getYear();
                    String line = null;
                    line = "BK" + e.backupNumbersp().getValue() + " " + e.getLineLetter() + e.id + "." + (CalculatedEntry.calculateGeneration(e.id, experimentalGen - 1, entriesList)) + " from " + e.getLineLetter() + e.id + "." + e.backupGeneration + " of " + date + " " + e.notes
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
                writer.write("Generation: "+i+"\r\n");

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
                        line = "BK" + e.backupNumbersp().getValue() + " " + e.getLineLetter() + e.id + "." + (CalculatedEntry.calculateGeneration(e.id, i - 1, entriesList)) + " from " + e.getLineLetter() + e.id + "." + e.backupGeneration + " of " + date + " " + e.notes
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
        }
    }

    /**
     * Creates a file displaying the sample number and sample generation up to the experimental generation that was input
     * @param numberOfGenerations
     */
    public void writeBackupTableDetailed(int numberOfGenerations){

        try
        {

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Sample Backups.csv"),"utf-8"));
            //print header
            String header = "Sample ID";
            for(int i=1;i<=numberOfGenerations;i++){
                header=header+","+i;
            }
            header=header+"\n";
            writer.write(header);
            //for each sample number
            for(int csample=500;csample<=999;csample++){

                ArrayList<Entry> sampleEntries = CalculatedEntry.getEntriesForSampleNumber(csample, this.entriesList);
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
        }

    }

    public void createEntryHistoryList(int sampleID){
        entryHistory.clear();
        ArrayList<Entry> entries = CalculatedEntry.getEntriesForSampleNumber(sampleID, entriesList);
        if(entries==null)return;
        Collections.sort(entries,new SortByDate());
        for(Entry e : entries){
            this.entryHistory.add(e);
        }
    }

//miscellaneous methods------------------------



}
