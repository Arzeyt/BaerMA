package com.BaerMA;

import com.BaerMA.DataObjects.CalculatedEntry;
import com.BaerMA.DataObjects.Entry;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class WordMaster {

    public static void printFormattedEntries(int generation){
        XWPFDocument document = new XWPFDocument();
        File file = new File(MainStage.settings.outputDirectory+File.separator+"Formatted Entries to gen "+generation+".docx");
        try{
            OutputStream outputStream = new FileOutputStream(file);
            for(int i=0; i<=generation; i++) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();


                //header for each generation
                String header = MainStage.settings.getFormattedEntriesListHeader(i)+"\r\n";

                System.out.println("Header: "+header);
                String[] headers = header.split("<");
                //comment
                int hhh=0;
                for(String hh : headers){
                    System.out.println(hhh+"]"+hh);
                    hhh++;
                }
                for(String h : headers){
                    if(h.length()>=2) {
                        String[] splitagain = h.split(">");
                        if(splitagain.length>=2) {
                            String colorString = splitagain[0];
                            System.out.println("ColorString: " + colorString);
                            XWPFRun headerRun = paragraph.createRun();
                            headerRun.setColor(colorString);
                            headerRun.setBold(true);
                            headerRun.setText(splitagain[1]);
                        }else{
                            XWPFRun headerRun = paragraph.createRun();
                            headerRun.setBold(true);
                            headerRun.setText(splitagain[0]);
                        }
                    }
                }

                ArrayList<Entry> entries = MainStage.entries.getEntriesForGeneration(i);

                //divide the entries list into extinct and non extinct
                ArrayList<Entry> nonextinct = new ArrayList<>();
                ArrayList<Entry> extinct = new ArrayList<>();
                for(Entry e : entries){
                    if(e.backupGeneration==-1){
                        extinct.add(e);
                    }else{
                        nonextinct.add(e);
                    }
                }
                //if there are entries in the nonextinct list, print them
                if(nonextinct.size()>0) {
                    for (Entry e : nonextinct) {
                        String date = e.backupOfDate.getMonthValue() + "/" + e.backupOfDate.getDayOfMonth() + "/" + e.backupOfDate.getYear();
                        String line = null;
                        line = "BK" + e.backupNumbersp().getValue() + " " + e.getLineLetter() + e.id + "." + (CalculatedEntry.calculateGeneration(e.id, i - 1)) + " from " + e.getLineLetter() + e.id + "." + e.backupGeneration + " of " + date + " " + e.notes
                                + "\r\n";
                        XWPFParagraph paragraph2 = document.createParagraph();
                        XWPFRun run2 = paragraph2.createRun();
                        run2.setText(line);
                    }
                }
                //if there are entires in the extinct list, print them
                if(extinct.size()>0) {
                    XWPFParagraph paragraph3=document.createParagraph();
                    XWPFRun run3 = paragraph3.createRun();
                    run3.setBold(true);
                    run3.setText("Extinctions");

                    for (Entry e : extinct) {
                        XWPFParagraph paragraph4 = document.createParagraph();
                        XWPFRun run4 = paragraph4.createRun();
                        run4.setText(e.getLineLetter() + e.id + " extinct " + e.notes + "\r\n");
                    }
                }
                //new line between gens

            }
            document.write(outputStream);
            outputStream.close();
            Desktop.getDesktop().open(file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
