package com.BaerMA.DataObjects;

public class PickerObject {

    public String name;

    public PickerObject(String Name){
        this.name = Name;
    }

    @Override
    public String toString() {
        return name;
    }

}
