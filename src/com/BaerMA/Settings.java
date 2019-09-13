package com.BaerMA;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;

public class Settings implements Serializable{

public int XTerminationGeneration=70;
public int J2LineGenesisGeneration = 71;
public File dataDirectory = new File("Data");
public File outputDirectory = new File("Output");
public File entriesFile = new File(dataDirectory+File.separator+"Entries.json");



    public Settings(){

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
}
