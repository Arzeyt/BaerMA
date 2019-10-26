package com.BaerMA.Sorting;

import com.BaerMA.DataObjects.EntriesJournalObject;

import java.util.Comparator;

public class SortByGen implements Comparator<EntriesJournalObject> {
    @Override
    public int compare(EntriesJournalObject o1, EntriesJournalObject o2) {
        if(o1.getToGen()<o2.getToGen()){
            return -1;
        }else if(o1.getToGen()==o2.getToGen()){
            return 0;
        }else{
            return 1;
        }
    }
}
