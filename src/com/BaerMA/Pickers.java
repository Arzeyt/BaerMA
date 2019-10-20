package com.BaerMA;

import com.BaerMA.DataObjects.PickerGenerationObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Pickers {

    private static ArrayList<PickerGenerationObject> pickers = new ArrayList<>();

    public void initialize() {
        load();
    }

    public void addPicker(PickerGenerationObject picker){
        pickers.add(picker);
        save();
    }

    public ArrayList<PickerGenerationObject> getPickersForGeneration(int generation){
        ArrayList<PickerGenerationObject> pickersForGen = new ArrayList<>();
        for(PickerGenerationObject picker: pickers){
            if(picker.generation==generation){
                pickersForGen.add(picker);
            }
        }

        return pickersForGen;
    }

    private ArrayList<PickerGenerationObject> fillList(ArrayList<PickerGenerationObject> pickers){
        for(PickerGenerationObject picker : pickers){
            
        }
    }
    public void save(){
        //only save if picker is selected
        pickers.removeIf(pickerGenerationObject -> {
            if(pickerGenerationObject.selected==false){
                return true;
            }else{
                return false;
            }
        });
        System.out.println(pickers);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        System.out.println(gson.toJson(this));
        try{
            FileWriter writer = new FileWriter(MainStage.settings.dataDirectory+ File.separator+"PickersForGenerations.json");
            writer.write(gson.toJson(this));
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void load(){
        try{
            FileReader reader = new FileReader(new File(MainStage.settings.dataDirectory+ File.separator+"PickersForGenerations.json"));
            Gson gson = new Gson();
            Pickers pickers = gson.fromJson(reader,new TypeToken<Pickers>(){}.getType());
            System.out.println(gson.toJson(pickers));
            MainStage.pickers=pickers; //replaces the empty pickers class with the loaded pickers

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
