package com.BaerMA;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Nick on 1/27/2019.
 */
public class Entry implements Serializable{
    int id, experimentalGeneration, backupGeneration;
    LocalDate backupOfDate, pickDate;
    public String notes, extra;


    /**
    //used to display data in the entryTable
    transient SimpleStringProperty sampleIDssp;
    public void setSampleIDssp(String value){ sampleIDsp().set(value);}
    public String getSampleIDssp(){return sampleIDsp().getValue();}
    public StringProperty sampleIDsp(){
        if(sampleIDssp ==null) sampleIDssp =new SimpleStringProperty(this,"sampleIDssp");
        return sampleIDssp;
    }
     */
    transient SimpleStringProperty linessp;
    public void setlinessp(String value){linesp().set(value);}
    public String getlinessp(){return linesp().getValue();}
    public StringProperty linesp(){
        if(linessp==null)linessp=new SimpleStringProperty(this,"linessp");
        return linessp;
    }

    transient SimpleIntegerProperty sampleIDssp;
    public void setSampleIDssp(Integer value){ sampleIDsp().set(value);}
    public Integer getSampleIDssp(){return sampleIDsp().getValue();}
    public IntegerProperty sampleIDsp(){
        if(sampleIDssp ==null) sampleIDssp =new SimpleIntegerProperty(this,"sampleIDssp");
        return sampleIDssp;
    }

    transient SimpleStringProperty SpickDate;
    public void setSpickDate(String value){SpickDateProperty().set(value);}
    public String getSpickDate(){return SpickDateProperty().getValue();}
    public StringProperty SpickDateProperty(){
        if(SpickDate==null) {
            SpickDate = new SimpleStringProperty(this, "SpickDate");
        }
        return SpickDate;
    }

    transient SimpleIntegerProperty SexperimentalGeneration;
    public void setSexperimentalGeneration(Integer value){SexperimentalGenerationProperty().set(value);}
    public Integer getSexperimentalGeneration(){return SexperimentalGenerationProperty().getValue();}
    public IntegerProperty SexperimentalGenerationProperty(){
        if(SexperimentalGeneration==null)SexperimentalGeneration=new SimpleIntegerProperty(this,"SexperimentalGeneration");
        return SexperimentalGeneration;
    }

    transient SimpleIntegerProperty SbackupGeneration;
    public void setSbackupGeneration(Integer value){SbackupGenerationProperty().set(value);}
    public Integer getSbackupGenerationProperty(){return SbackupGenerationProperty().getValue();}
    public SimpleIntegerProperty SbackupGenerationProperty(){
        if(SbackupGeneration==null)SbackupGeneration=new SimpleIntegerProperty(this,"SbackupGeneration");
        return SbackupGeneration;
    }

    transient SimpleStringProperty SbackupOfDate;
    public void setSbackupOfDate(String value){SbackupOfDateProperty().set(value);}
    public String getSbackupOfDate(){return SbackupOfDateProperty().getValue();}
    public StringProperty SbackupOfDateProperty(){
        if(SbackupOfDate==null)SbackupOfDate=new SimpleStringProperty(this,"SbackupOfDate");
        return SbackupOfDate;
    }

    transient SimpleStringProperty Snotes;
    public void setSnotes(String value){SnotesProperty().set(value);}
    public String getSnotes(){return SnotesProperty().getValue();}
    public StringProperty SnotesProperty(){
        if(Snotes==null)Snotes=new SimpleStringProperty(this,"Snotes");
        return Snotes;
    }

    public transient SimpleIntegerProperty SbackupNumber;
    public void setSbackupNumber(Integer value){ backupNumbersp().set(value);}
    public Integer getSbackupNumber(){return backupNumbersp().getValue();}
    public IntegerProperty backupNumbersp(){
        if(SbackupNumber ==null) SbackupNumber =new SimpleIntegerProperty(this,"SbackupNumber");
        return SbackupNumber;
    }

    public Entry(int sampleID, int experimentalGeneration, LocalDate pickDate, int backupGeneration, LocalDate backupOfDate, String notes){


        this.id=sampleID;
        this.experimentalGeneration=experimentalGeneration;
        this.pickDate=pickDate;
        this.backupGeneration = backupGeneration;
        this.backupOfDate=backupOfDate;
        this.notes=notes;

        this.linessp=new SimpleStringProperty(getLineLetter());
        this.sampleIDssp = new SimpleIntegerProperty(sampleID);
        this.SexperimentalGeneration= new SimpleIntegerProperty(experimentalGeneration);
        this.SpickDate=new SimpleStringProperty(pickDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        this.SbackupGeneration=new SimpleIntegerProperty(backupGeneration);

        //extinct backups are displayed as having dates equal to the pick date
        if(backupGeneration==-1){
            this.SbackupOfDate=new SimpleStringProperty(pickDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }else{
            this.SbackupOfDate=new SimpleStringProperty(backupOfDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }

        this.Snotes=new SimpleStringProperty(notes+"");

        //System.out.println("sampleID: "+ sampleIDssp +"\nSexperimentalGeneration: "+SexperimentalGeneration+" \nSbackupGeneration: "+SbackupGeneration+"\nSbackupOfDate: "+SbackupOfDate+" \nSnotes: "+Snotes+" \nSbackupNumber"+SbackupNumber);
    }


    @Override
    public String toString() {

        return "\n-------\n" +
                "sample id: "+id+"        experimental generation: "+ experimentalGeneration +"       date: "+pickDate+"\n"+
                "backup from sample generation: "+backupGeneration+" of: "+backupOfDate+"\n"+
                "Notes: "+notes+
                "\n-------\n";
    }
    public boolean equalsID(int id){

        return this.id==id;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Entry && ((Entry) obj).experimentalGeneration==this.experimentalGeneration && ((Entry)obj).id==this.id){
            return true;
        }else{
            return false;
        }
    }

    public void setBackupNumber(int backupNumber){
        this.SbackupNumber=new SimpleIntegerProperty(backupNumber);
    }

    public String getLineLetter(){

        ArrayList<LineObject> lines = MainStage.settings.lines;
        for(LineObject line : lines){
            if(id>=line.lineStartNumber && id <= line.lineEndNumber){
                return line.lineName;
            }
        }
        return "NA";
    }

    /**
     * @param sampleID
     * @param experimentalGeneration
     * @return the backup number (failure combo) of this sampleID for this experimentalGeneration. this method is only useful for an
     * Entry, and not for a Calculated entry.
     */
    public static int getBackupNumberForEntry(int sampleID, int experimentalGeneration) {
        ArrayList<Entry> sampleEntries = Entries.getEntriesForSampleNumber(sampleID);
        if(sampleEntries==null){return 0;}

        int exGen=0;
        int backupNumber=0;
        for(Entry e : sampleEntries){
            if(e.experimentalGeneration>=experimentalGeneration){
                return 0;
            }else if(exGen==0){
                exGen=e.experimentalGeneration;
            }else{

            }

        }
        return backupNumber;
    }
}
