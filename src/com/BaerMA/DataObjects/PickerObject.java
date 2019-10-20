package com.BaerMA.DataObjects;

public class PickerObject {

    public String Name;

    public PickerObject(String Name){
        this.Name = Name;
    }

    @Override
    public String toString() {
        return Name;
    }

}
