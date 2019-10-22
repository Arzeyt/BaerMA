package com.BaerMA.DataObjects;

import com.BaerMA.MainStage;
import com.BaerMA.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains an integer (generation) with an ArrayList of PickerObjects associated with the generation
 * Mimics HashMap<Integer,ArrayList<PickerObject>> but has the benefit of saving and easy accessor methods
 */
public class PickerGenerationMapData {

    HashMap<Integer,ArrayList<PickerObject>> pickerGenerationMap;
    transient String path = MainStage.settings.dataDirectory+ File.separator+"Pickers Generation Data";

    public ArrayList<PickerObject> getPickersForGen(int generation){
        if(pickerGenerationMap==null){
            System.out.println("Picker Generation Data is null. Creating new map.");
            pickerGenerationMap=new HashMap<>();
        }
        if(pickerGenerationMap.get(generation)==null){
            return new ArrayList<PickerObject>();
        }else{
            return pickerGenerationMap.get(generation);
        }
    }

    public void addPickerToGeneration(PickerObject picker, int generation){
        if(pickerGenerationMap.get(generation)==null){
            ArrayList<PickerObject> pickersForGen = new ArrayList<>();
            pickersForGen.add(picker);
            pickerGenerationMap.put(generation,pickersForGen);
        }else{
            pickerGenerationMap.get(generation).add(picker);
        }
        System.out.println("added: "+picker.Name+" to generation: "+generation+". Map size: "+pickerGenerationMap.size());
        save();
    }

    public void removePickerFromGeneration(PickerObject picker, int generation){
        pickerGenerationMap.get(generation).remove(picker);
        System.out.println("removed: "+picker.Name+" from gen: "+generation);
        save();
    }
    public void load(){
        try{
            FileReader reader = new FileReader(new File(path));
            Gson gson = new Gson();
            PickerGenerationMapData pickerGenerationMapData = gson.fromJson(reader,new TypeToken<PickerGenerationMapData>(){}.getType());
            System.out.println(gson.toJson(pickerGenerationMapData));
            MainStage.pickerGenerationMapData=pickerGenerationMapData;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        Gson gson = new Gson();
        try{
            FileWriter writer = new FileWriter(path);
            writer.write(gson.toJson(this));
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
