package com.BaerMA.DataObjects;

public class EntriesJournalObject {

    String header="";
    int toGen;

    public EntriesJournalObject(String header, int toGen){
        this.header=header;
        this.toGen=toGen;
    }

    public String getHeader(){
        return header;
    }

    public int getToGen(){
        return toGen;
    }


}
