package com.BaerMA.DataObjects;

public class LineObject {
    public String lineName;
    public int lineStartNumber;
    public int lineEndNumber;

    public LineObject(String lineName, int lineStartNumber, int lineEndNumber){
        this.lineName=lineName;
        this.lineStartNumber=lineStartNumber;
        this.lineEndNumber=lineEndNumber;
    }
}
