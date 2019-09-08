package com.BaerMA;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;

public class CalculatedEntry {


    public static String sampleIDString = "sampleID", calculatedGenerationString = "calculatedGeneration", backupCountString = "backupCount",
        resetCountString = "resetCount", backupNumberString = "backupNumber";


    SimpleIntegerProperty sampleID;
    public void setSampleID(Integer value){
        sampleIDProperty().set(value);}
    public Integer getsampleID(){return sampleIDProperty().getValue();}
    public IntegerProperty sampleIDProperty(){
        if(sampleID ==null) sampleID =new SimpleIntegerProperty(this,sampleIDString);
        return sampleID;
    }

    SimpleIntegerProperty calculatedGeneration;
    public void setcalculatedGeneration(Integer value){calculatedGenerationProperty().set(value);}
    public Integer getcalculatedGeneration(){return calculatedGenerationProperty().getValue();}
    public IntegerProperty calculatedGenerationProperty(){
        if(calculatedGeneration ==null) calculatedGeneration =new SimpleIntegerProperty(this,calculatedGenerationString);
        return calculatedGeneration;
    }


    SimpleIntegerProperty backupCount;
    public void setBackupCount(Integer value){backupCountProperty().set(value);}
    public Integer getBackupCount(){return backupCountProperty().getValue();}
    public IntegerProperty backupCountProperty(){
        if(backupCount==null)backupCount=new SimpleIntegerProperty(this,"backupCount");
        return backupCount;
    }

    SimpleIntegerProperty resetCount;
    public void setresetCount(Integer value){resetCountProperty().set(value);}
    public Integer getresetCount(){return resetCountProperty().getValue();}
    public IntegerProperty resetCountProperty(){
        if(resetCount==null)resetCount=new SimpleIntegerProperty(this,"resetCount");
        return resetCount;
    }

    SimpleIntegerProperty backupNumber;
    public void setbackupNumber(Integer value){
        backupNumberProperty().set(value);}
    public Integer getbackupNumber(){return backupNumberProperty().getValue();}
    public IntegerProperty backupNumberProperty(){
        if(backupNumber ==null) backupNumber =new SimpleIntegerProperty(this,backupNumberString);
        return backupNumber;
    }


    public CalculatedEntry(int sampleID, int experimentalGeneration, ObservableList<Entry> entries){
        this.sampleID =new SimpleIntegerProperty(sampleID);

        this.backupNumber = new SimpleIntegerProperty(getBackupNumberForEntry(sampleID,experimentalGeneration,entries));

        int calculatedGeneration = calculateGeneration(sampleID,experimentalGeneration,entries);
        this.calculatedGeneration =new SimpleIntegerProperty(calculatedGeneration);

        //for efficiency's sake, use the function that calculates both backup and reset count at the same time.
        Integer[] bandr = getBackupAndResetNumberForEntry(sampleID,experimentalGeneration,entries);
        this.backupCount=new SimpleIntegerProperty(bandr[0]);
        this.resetCount=new SimpleIntegerProperty(bandr[1]);


    }

    public static Integer calculateGeneration(int sampleNumber, int experimentalGeneration, ObservableList<Entry> entries){
        ArrayList<Entry> entryList = getEntriesForSampleNumber(sampleNumber);

        //if there are no entries for this sample number, just return the experimental generation
        if(entryList==null){
            return experimentalGeneration;
        }

        //ensure that entries with experimental generations larger than the input are ignored, but the largest one before that is picked
        entryList.sort(new SortByDate());
        Entry largestValidEntry = entryList.get(0);

        for(int i = 0; i<entryList.size(); i++){
            Entry e = entryList.get(i);
            if(e.experimentalGeneration<=experimentalGeneration){
                largestValidEntry=e;
            }else{
                break;
            }
        }
        //make sure largest valid entry's experimental generation does not exceed the experimental generation input.
        //If it does, this means that there are no entries for this sample before the inputted experimental generation
        //and we should return the experimental generation value, because we're assuming no backups have been made up to this point
        if(largestValidEntry.experimentalGeneration>experimentalGeneration){
            return experimentalGeneration;
        }

        //if the sample is extinct by this point, the largest valid entry will contain a value of -1 for the backup generation. In this case,
        // we return the same -1 value at each generation after this point, and avoid incrementing it in the future to denote the extinction.
        if(largestValidEntry.backupGeneration==-1){
            return -1;
        }

        //X line Termination. If the sample is an X, and the expGen >= Settings.XTerminationGeneration, return -1
        if(sampleNumber>=700 && sampleNumber<=799 && experimentalGeneration>=Settings.XTerminationGeneration){
            return -1;
        }

        //the final calculation
        int calculatedGeneration = experimentalGeneration-largestValidEntry.experimentalGeneration+largestValidEntry.backupGeneration+1;

        /**
        System.out.println("Entry list size: "+entryList.size());
        System.out.println("First Entry in sorted list is: "+entryList.get(0));
        System.out.println("largest valid entry is: "+largestValidEntry);
        System.out.println("current generation for this sample is: "+calculatedGeneration);
*/
        return calculatedGeneration;

    }

    public static ArrayList<Entry> getEntriesForSampleNumber(int sampleNumber){
        ArrayList<Entry> entries = new ArrayList<>();
        for(Entry e : Entries.entriesList){
            if(e.id==sampleNumber) {
                entries.add(e);
            }
        }
        if(entries.size()<1){
            return null;
        }else{
            return entries;
        }
    }


    @Override
    public String toString() {
        return "sampleID: "+ sampleID.getValue()+" calculated generation: "+ calculatedGeneration.getValue()+" backupCount: "+backupCount.getValue()+" resetCount: "+resetCount.getValue();
    }

    public Integer getNumberOfBackupsForEntry(int sampleID, int experimentalGeneration, ObservableList<Entry> oEntries){
        ArrayList<Entry> entries = CalculatedEntry.getEntriesForSampleNumber(sampleID);
        if(entries==null)return 0;
        Collections.sort(entries,new SortByDate());
        int backupCounter = 0;
        for(Entry e : entries){
            if(e.experimentalGeneration<=experimentalGeneration){
                backupCounter++;
            }
        }

        return backupCounter;
    }

    public Integer getResetNumberForEntry(int sampleID, int experimentalGeneration, ObservableList<Entry> oEntries){
        ArrayList<Entry> entries = CalculatedEntry.getEntriesForSampleNumber(sampleID);
        if(entries==null)return 0;
        Collections.sort(entries,new SortByDate());
        int resetCounter = 0;
        for(Entry e : entries){
            if(e.experimentalGeneration<=experimentalGeneration && e.backupGeneration==0 && e.experimentalGeneration>=3){
                resetCounter++;
            }
        }

        return resetCounter;
    }

    /**
     *
     * @param sampleID
     * @param experimentalGeneration
     * @return Int[2] array where [0] is backup number and [1] is reset number. Backups and resets are counted seperately. 0,0 if no entries
     */
    public static Integer[] getBackupAndResetNumberForEntry(int sampleID, int experimentalGeneration, ObservableList<Entry> oEntries){
        Integer[] bandr = new Integer[2];
        ArrayList<Entry> entries = CalculatedEntry.getEntriesForSampleNumber(sampleID);
        if(entries==null){
            bandr[0] = 0;
            bandr[1] = 0;
            return bandr;
        }

        int backupCounter = 0;
        int resetCounter = 0;

        for(Entry e : entries){
            if(e.experimentalGeneration<=experimentalGeneration){
                if(e.backupGeneration==0 && e.experimentalGeneration>=3) {
                    resetCounter++;
                }else{
                    backupCounter++;
                }
            }
        }
        bandr[0]=backupCounter;
        bandr[1]=resetCounter;

        return bandr;
    }

    //shouldn't do it this way due to high overhead

    public static int getBackupNumberForEntry(int sampleID, int experimentalGeneration, ObservableList<Entry> entries) {
        ArrayList<Entry> sampleEntries = getEntriesForSampleNumber(sampleID);
        if(sampleEntries==null){return 0;}

        int exgen=0;
        int backupNumber=0;
        for(Entry e : sampleEntries){
            if(e.experimentalGeneration>=experimentalGeneration){
                return 0;
            }else if(exgen==0){
                exgen=e.experimentalGeneration;
            }else{

            }

        }
        return backupNumber;
    }



}
