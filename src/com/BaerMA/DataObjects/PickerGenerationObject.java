package com.BaerMA.DataObjects;

public class PickerGenerationObject {
    public int generation;
    public String name;
    public boolean selected;

    public PickerGenerationObject(String name, boolean selected, int generation){
        this.name=name;
        this.selected=selected;
        this.generation=generation;
    }


}
