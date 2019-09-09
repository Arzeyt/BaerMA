package com.BaerMA;

import javafx.scene.Parent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;

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
       try(InputStream inputStream = new FileInputStream(baerSheet)){
           XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(inputStream);
           XSSFSheet sheet = wb.getSheetAt(0);
           System.out.println("editing sheet: "+sheet.getSheetName());
           XSSFRow row = sheet.getRow(0);
           XSSFCell cell = row.getCell(0);
           System.out.println("Cell 0,0 is: "+cell.getStringCellValue());
           //set generation
           row = sheet.getRow(2);
           cell = row.getCell(5);
           cell.setCellValue("Baer MA Generation #: "+experimentalGeneration);
           //set Date
           LocalDate date = Entries.getDateForGeneration(experimentalGeneration);
           row = sheet.getRow(2);
           cell=row.getCell(0);
           cell.setCellValue("Date: "+date.getMonthValue()+"-"+date.getDayOfMonth()+"-"+date.getYear());

           //Calc 1 page
           sheet=wb.getSheetAt(1);

           System.out.println("editing sheet: "+sheet.getSheetName());


           //create an arraylist for each calculated entry
           ArrayList<CalculatedEntry> calculatedEntries=new ArrayList<>();

           //for pre-J2 sheet
           for(int i =500; i<=1099; i++){
               CalculatedEntry calculatedEntry = new CalculatedEntry(i,experimentalGeneration, Entries.entriesList);
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
       if(experimentalGen<Settings.J2LineGenesisGeneration){
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

   //generation history excel sheet
   public static void createGenerationVisualizer(int experimentalGeneration){
        XSSFWorkbook wb = new XSSFWorkbook();
        File visualizerFile = new File("Generation Visualizer for gen: "+experimentalGeneration);
        try{
            OutputStream outputStream = new FileOutputStream(visualizerFile);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
