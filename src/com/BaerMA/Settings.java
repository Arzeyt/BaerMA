package com.BaerMA;

import com.BaerMA.DataObjects.EntriesJournalObject;
import com.BaerMA.DataObjects.LineObject;
import com.BaerMA.DataObjects.PickerObject;
import com.BaerMA.Sorting.SortByGen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Settings implements Serializable{

    public boolean useCloudStorage=false;
    public int XTerminationGeneration=70;
    public int J2LineGenesisGeneration = 71;
    public File dataDirectory = new File("Data");
    public File outputDirectory = new File("Output");
    public File entriesFile = new File(dataDirectory+File.separator+"Entries.json");
    public ArrayList<LineObject> lines = new ArrayList<>();
    public ArrayList<PickerObject> pickers = new ArrayList<>();
    public ArrayList<EntriesJournalObject> entriesJournalList = new ArrayList<>();



    public Settings(){
        //default lines
        lines.add(new LineObject("C",500,599));
        lines.add(new LineObject("M",600,699));
        lines.add(new LineObject("X",700,799));
        lines.add(new LineObject("J",800,899));
        lines.add(new LineObject("E",900,999));
        lines.add(new LineObject("J2",1000,1099));

        //default pickers
        pickers.add(new PickerObject("Nicholas Edenhoffer"));
        pickers.add(new PickerObject("Vaishali Katju"));

        //default  entriesJournalList
        entriesJournalList.add(new EntriesJournalObject("$date \r\n BaerMA Generation $generation \r\n Transferred as per MA protocol; Done by $names",400));

    }

    public void initialize(){
        File settingsFile = new File("Settings.json");
        if(settingsFile.exists()){
            System.out.println("Loading Settings.json");
            loadSettings();

        }else{
            System.out.println("Writing default Settings.json");
            writeDefaultSettings();

        }
    }

    private void loadSettings(){
        try{
            FileReader reader = new FileReader(new File("Settings.json"));
            Gson gson = new Gson();
            Settings settings = gson.fromJson(reader,new TypeToken<Settings>(){}.getType());
            System.out.println(gson.toJson(settings));
            if(settings==null){
                System.out.println("settings is null. Recreating default settings file");
                writeDefaultSettings();
            }else{
                MainStage.settings=settings;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultSettings(){
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        System.out.println(gson.toJson(new Settings()));
        try {
            FileWriter fileWriter = new FileWriter("Settings.json");
            fileWriter.write(gson.toJson(new Settings()));
            fileWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        System.out.println(gson.toJson(this));
        try{
            FileWriter writer = new FileWriter("Settings.json");
            writer.write(gson.toJson(this));
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFormattedEntriesListHeader(int generation){
        String x = MainStage.pickerGenerationMapData.getFormattedStringForGen(generation);
        entriesJournalList.sort(new SortByGen().reversed());
        EntriesJournalObject journalObject=null;
        if(entriesJournalList.size()<1){
            journalObject=new EntriesJournalObject("",99999);
        }else {
            for (EntriesJournalObject j : entriesJournalList) {
                if (j.getToGen() >= generation) {
                    if(journalObject==null){
                        journalObject=j;
                    }else if (j.getToGen() - generation < journalObject.getToGen() - generation) {
                        journalObject = j;
                    }
                }
                //pick the journal object that's equal to, or closest to, but greater than, the other journalObjects.

            }
        }
        String s = journalObject.getHeader().replace("$names",x);
        LocalDate date = MainStage.entries.getDateForGeneration(generation);
        s=s.replace("$date",date.getMonthValue()+"/"+date.getDayOfMonth()+"/"+date.getYear());
        s=s.replace("$generation",generation+"");
        return s;
    }
}
