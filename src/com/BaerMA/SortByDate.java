package com.BaerMA;

import com.BaerMA.DataObjects.Entry;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Created by Nick on 1/28/2019.
 */

/**
 * Smallest experimental generation first (at index 0)
 */
public class SortByDate implements Comparator<Entry> {
    @Override
    public int compare(Entry e1, Entry e2) {
        LocalDate date1 = e1.pickDate;
        LocalDate date2 = e2.pickDate;

        if(date1.isBefore(date2)){
            return -1;
        }else if(date1.equals(date2)){
            return 0;
        }else{
            return 1;
        }
    }
}
