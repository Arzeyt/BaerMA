package com.BaerMA.DataObjects;

/**
 * Created by Nick on 1/27/2019.
 */
public class DateObject {
    public int day, month, year;

    public DateObject(int month, int day, int year){
        this.day=day;
        this.month=month;
        this.year=year;
    }

    @Override
    public String toString() {
        return this.month+"/"+this.day+"/"+this.year;
    }
}
