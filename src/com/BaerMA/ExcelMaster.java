package com.BaerMA;

import javafx.scene.Parent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
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

   public static void createBaerSheet(int experimentalGen){
       File baerSheet = createBaerSheetCopy(experimentalGen);
       try(InputStream inputStream = new FileInputStream(baerSheet)){
           XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(inputStream);
           XSSFSheet sheet = wb.getSheetAt(0);
           System.out.println("editing sheet: "+sheet.getSheetName());
           XSSFRow row = sheet.getRow(0);
           XSSFCell cell = row.getCell(0);
           System.out.println("Cell 0,0 is: "+cell.getStringCellValue());

           //Calc 1 page
           sheet=wb.getSheetAt(1);

           System.out.println("editing sheet: "+sheet.getSheetName());

           //create an arraylist for each calculated entry
           ArrayList<CalculatedEntry> calculatedEntries=new ArrayList<>();

           for(int i =500; i<1000; i++){
               CalculatedEntry calculatedEntry = new CalculatedEntry(i,experimentalGen, Entries.entriesList);
               calculatedEntries.add(calculatedEntry);
           }

           System.out.println(calculatedEntries.size());

           int rowi = 2;
           int celli = 0;
           row = sheet.getRow(rowi);
           cell = row.getCell(celli);

           for(CalculatedEntry e : calculatedEntries){
               cell.setCellValue(e.sampleID.getValue());
               System.out.println(cell.getNumericCellValue());
               //right one
               cell = row.getCell(1);
               cell.setCellValue(e.calculatedGeneration.getValue());
               System.out.println(cell.getNumericCellValue());

               //increment row and reset column to 0
               rowi++;
               row=sheet.getRow(rowi);
               cell=row.getCell(0);
           }

           //write it out
           try {
               OutputStream outputStream = new FileOutputStream(baerSheet);
               wb.write(outputStream);
           }catch(Exception e){
               e.printStackTrace();
           }

           wb.close();
           Desktop.getDesktop().open(baerSheet);


       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public static File createBaerSheetCopy(int experimentalGen){
       File baerTemplate = new File("BaerMA Template.xlsx");
       if(baerTemplate.exists()){
           System.out.println("BaerMA Template.xlsx found");
       }else{
           System.out.println("BaerMA Template.xlsx not found. Please replace this file.");
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


}
