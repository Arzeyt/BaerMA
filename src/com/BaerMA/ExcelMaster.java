package com.BaerMA;

import javafx.scene.Parent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelMaster {

   public static void createWorkbook(){
       Workbook workbook = new HSSFWorkbook();

       try(OutputStream fileOut = new FileOutputStream("workbook.xls")){
           workbook.write(fileOut);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   //baer sheet----------------------
   public static void createBaerSheetUpTo(int experimentalGeneration){
       for(int i = 0; i<=experimentalGeneration; i++){
           createBaerSheet(i, false);
       }
   }
   public static void createBaerSheet(int experimentalGeneration, boolean open){
       File baerSheet = createBaerSheetCopy(experimentalGeneration);
       try(InputStream inputStream = new FileInputStream(baerSheet)) {
           XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(inputStream);
           XSSFSheet sheet = wb.getSheetAt(0);
           System.out.println("editing sheet: " + sheet.getSheetName());
           XSSFRow row = sheet.getRow(0);
           XSSFCell cell = row.getCell(0);
           System.out.println("Cell 0,0 is: " + cell.getStringCellValue());
           //set generation
           row = sheet.getRow(2);
           cell = row.getCell(5);
           cell.setCellValue("Baer MA Generation #: " + experimentalGeneration);
           //set Date
           LocalDate date = Entries.getDateForGeneration(experimentalGeneration);
           row = sheet.getRow(2);
           cell = row.getCell(0);
           cell.setCellValue("Date: " + date.getMonthValue() + "-" + date.getDayOfMonth() + "-" + date.getYear());

           //Calc 1 page
           sheet = wb.getSheetAt(1);

           System.out.println("editing sheet: " + sheet.getSheetName());


           //create an arraylist for each calculated entry
           ArrayList<CalculatedEntry> calculatedEntries = new ArrayList<>();

           int max = 999;
           //for pre-J2 sheet
           if (experimentalGeneration >= MainStage.settings.J2LineGenesisGeneration) max=1099;
           for (int i = 500; i <= max; i++) {
               CalculatedEntry calculatedEntry = new CalculatedEntry(i, experimentalGeneration, Entries.entriesList);
               calculatedEntries.add(calculatedEntry);
           }


           System.out.println(calculatedEntries.size());

           int rowi = 2;
           int columni = 0;

           for(CalculatedEntry e : calculatedEntries){

               row = sheet.getRow(rowi);
               cell = row.getCell(columni);

               cell.setCellValue(e.sampleID.getValue());
               System.out.println(cell.getNumericCellValue());
               //right one
               row.createCell(1);
               cell = row.getCell(1);
               System.out.println("Writing cell: "+cell.getRowIndex()+" "+cell.getColumnIndex());
               //format extinct
               if(e.calculatedGeneration.getValue()==-1){
                   cell.setCellType(CellType.STRING);
                   cell.setCellValue("Extinct");
               }else {
                   cell.setCellValue(e.calculatedGeneration.getValue());
               }

               //increment row and reset column to 0
               rowi++;
               columni=0;
           }

           //write it out
           try {
               OutputStream outputStream = new FileOutputStream(baerSheet);
               wb.write(outputStream);
               outputStream.close();
           }catch(Exception e){
               e.printStackTrace();
           }

           wb.close();
           inputStream.close();
           if(open) Desktop.getDesktop().open(baerSheet);


       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   public static File createBaerSheetCopy(int experimentalGen){
       File baerTemplate = null;
       if(experimentalGen< MainStage.settings.J2LineGenesisGeneration){
            baerTemplate = new File("BaerMA Template X.xlsx");
       }else{
           baerTemplate = new File("BaerMA Template J2.xlsx");
       }
       if(baerTemplate.exists()){
           System.out.println(baerTemplate.getName()+" found");
       }else{
           System.out.println(baerTemplate.getName()+" not found. Please replace this file.");
       }
       //copy the file
       File baerFile = new File(Entries.outputFile+File.separator+"BaerMA Gen "+experimentalGen+".xlsx");
       try {
           Files.copy(baerTemplate.toPath(),baerFile.toPath());
       } catch (IOException e) {
           e.printStackTrace();
       }

       return baerFile;
   }

   //generation visualizer excel sheet
   public static void createGenerationVisualizer(int experimentalGeneration){
       //threaded to allow for progress indicator incrementing
       Thread thread = new Thread("ExcelPrint"){
            public void run(){
                XSSFWorkbook wb = new XSSFWorkbook();
                File visualizerFile = new File(MainStage.settings.outputDirectory+File.separator+"Generation Visualizer for gen "+experimentalGeneration+".xlsx");
                Sheet sheet = wb.createSheet();
                int numberOfRows=650;
                //create 650 rows
                for(int i=0;i<=numberOfRows;i++){sheet.createRow(i);}

                //generation header
                sheet.addMergedRegion(new CellRangeAddress(0,0,1,experimentalGeneration+1));
                sheet.getRow(0).createCell(1).setCellValue("Generation");
                CellStyle centered = wb.createCellStyle();
                centered.setAlignment(HorizontalAlignment.CENTER);
                sheet.getRow(0).getCell(1).setCellStyle(centered);

                //sampleID header
                sheet.getRow(1).createCell(0).setCellValue("Sample ID");

                //generation header
                for(int i=0;i<=experimentalGeneration;i++){
                    sheet.getRow(1).createCell(i+1).setCellValue(i);
                }


                //Cell styles
                CellStyle styleYellow = wb.createCellStyle();
                styleYellow.setFillForegroundColor(IndexedColors.YELLOW.index);
                styleYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                CellStyle styleGreen = wb.createCellStyle();
                styleGreen.setFillForegroundColor(IndexedColors.GREEN.index);
                styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                CellStyle styleRed = wb.createCellStyle();
                styleRed.setFillForegroundColor(IndexedColors.RED.index);
                styleRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                CellStyle styleBlack = wb.createCellStyle();
                styleBlack.setFillForegroundColor(IndexedColors.BLACK.index);
                styleBlack.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                //iterate through each row and column and add the calculated generation value
                for(int r=2;r<=1099-500+2;r++){
                    //progress calculation--
                    float progress= (float) ((double) r/(1099-500+2));
                    System.out.println("progress is: "+progress);
                    MainStage.controller.setSLPrintProgress(progress);
                    //---

                    int sampleID = r-2+500;
                    for(int c=0;c<=experimentalGeneration+1;c++){
                        if(c==0){
                            System.out.println(r+" "+c);
                            sheet.getRow(r).createCell(c).setCellValue(sampleID);

                        }else{
                            int value = new CalculatedEntry(sampleID,c-1,Entries.entriesList).calculatedGeneration.getValue();
                            sheet.getRow(r).createCell(c).setCellValue(value);
                            if(c!=0) {
                                if (value == -2) {
                                    sheet.getRow(r).getCell(c).setCellStyle(styleBlack);
                                }else if (value == -1) {
                                    sheet.getRow(r).getCell(c).setCellStyle(styleRed);
                                }else if (value <= sheet.getRow(r).getCell(c - 1).getNumericCellValue() && c!=1) {
                                    sheet.getRow(r).getCell(c).setCellStyle(styleYellow);
                                }else{
                                    //sheet.getRow(r).getCell(c).setCellStyle(styleGreen);
                                }
                            }
                        }
                    }
                }

                //resize columns
                Iterator it = sheet.getRow(1).cellIterator();
                while(it.hasNext()){
                    Cell next = (Cell) it.next();
                    if(next.getColumnIndex()>=1){
                        sheet.setColumnWidth(next.getColumnIndex(), 800);
                        //sheet.setColumnWidth(next.getColumnIndex(), (int) (String.valueOf(next.getColumnIndex()-1).length()*400));
                    }
                }

                //write file
                try{
                    OutputStream outputStream = new FileOutputStream(visualizerFile);
                    wb.write(outputStream);
                    outputStream.close();
                    wb.close();
                    Desktop.getDesktop().open(visualizerFile);
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
       };
       thread.start();


   }


}
