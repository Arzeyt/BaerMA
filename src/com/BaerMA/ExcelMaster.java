package com.BaerMA;

import com.BaerMA.DataObjects.CalculatedEntry;
import com.BaerMA.DataObjects.LineObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.*;
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
       Thread thread = new Thread(){
           @Override
           public void run() {
               for(int i = 0; i<=experimentalGeneration; i++){
                   MainStage.controller.setSLPrintProgress((float)i/experimentalGeneration);
                   createBaerSheet(i, false);
               }
           }
       };
       thread.start();

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

           //worm workers
           wb.getSheetAt(0).getRow(2).getCell(11).setCellValue("Worm Workers: "+ MainStage.pickerGenerationMapData.getFormattedStringForGen(experimentalGeneration));


           //Calc 1 page
           sheet=wb.getSheetAt(1);
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
       if(baerFile.exists()){
           System.out.println("This file exists");
       }else{
           System.out.println("this file doesn't exist");
       }
       try {
           if(Entries.outputFile.exists()==false){
               Entries.outputFile.mkdir();
           }
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

    //generation visualizer excel sheet
    public static void createAverageBackupGraphingSheet(int experimentalGeneration){
        //threaded to allow for progress indicator incrementing
        Thread thread = new Thread("ExcelPrint"){
            public void run(){
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet();
                File statsFile = new File(MainStage.settings.outputDirectory+File.separator+"Average Generations Behind for Gen "+experimentalGeneration+".xlsx");

                //rows to create = number of lines + buffer (is this necessary or helpful?)
                for(int i=0;i<=MainStage.settings.lines.size()+100;i++){
                    for(int x=0;x<=experimentalGeneration+5;x++) {
                        sheet.createRow(i).createCell(x);
                    }
                }
                //header
                sheet.getRow(0).createCell(1).setCellValue("Generation");
                sheet.addMergedRegion(new CellRangeAddress(0,0,1,experimentalGeneration+3));
                CellStyle centered = wb.createCellStyle();
                centered.setAlignment(HorizontalAlignment.CENTER);
                sheet.getRow(0).getCell(1).setCellStyle(centered);

                sheet.getRow(1).createCell(0).setCellValue("Line");
                for(int i=1;i<=experimentalGeneration+1;i++){
                    sheet.getRow(1).createCell(i).setCellValue(i-1);
                }


                //data
                int numberOfLines = MainStage.settings.lines.size();
                int progressTarget = numberOfLines*experimentalGeneration;
                int progressCounter = 0;

                for(int r = 2;r<=numberOfLines+1;r++){
                    LineObject currentLine = MainStage.settings.lines.get(r-2);
                    for(int g = 0;g<=experimentalGeneration+1;g++){
                        progressCounter++;
                        MainStage.controller.setSLPrintProgress((float)progressCounter/progressTarget);
                        int currentGen = g-1;
                        if(g==0){
                            sheet.getRow(r).createCell(g).setCellValue(currentLine.lineName);
                        }else{
                            double[] stats = MainStage.entries.generationsBehindStats(currentLine.lineStartNumber,currentLine.lineEndNumber,currentGen,false);
                            sheet.getRow(r).createCell(g).setCellValue(stats[0]);
                        }
                    }
                }

                //chart
                XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0,0,0,0,0,10,15,25);
                XSSFChart chart = drawing.createChart(anchor);
                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.RIGHT);
                XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                categoryAxis.setTitle("Experimental Generations");
                categoryAxis.setMinorUnit(5);
                categoryAxis.setMajorUnit(10);
                categoryAxis.setMajorTickMark(AxisTickMark.CROSS);
                categoryAxis.setMinorTickMark(AxisTickMark.OUT);


                XDDFValueAxis valueAxis=chart.createValueAxis(AxisPosition.LEFT);
                valueAxis.setTitle("Average Generations Behind");
                valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);

                XDDFLineChartData chartData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, categoryAxis,valueAxis);

                XDDFNumericalDataSource<Double> dataGenerations = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1,1,1,experimentalGeneration+1));
                //line data hard coded. Consider not hard coding it.
                XDDFNumericalDataSource<Double> line1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(2,2,1,experimentalGeneration+1));
                XDDFNumericalDataSource<Double> line2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(3,3,1, experimentalGeneration+1));
                XDDFNumericalDataSource<Double> line3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(4,4,1,experimentalGeneration+1));
                XDDFNumericalDataSource<Double> line4 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(5,5,1,experimentalGeneration+1));
                XDDFNumericalDataSource<Double> line5 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(6,6,1,experimentalGeneration+1));
                XDDFNumericalDataSource<Double> line6 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(7,7,1,experimentalGeneration+1));

                XDDFLineChartData.Series allSeries;
                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line1);
                allSeries.setTitle(sheet.getRow(1).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),2,0,true,true));
                allSeries.setMarkerStyle(MarkerStyle.NONE);

                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line2);
                allSeries.setMarkerStyle(MarkerStyle.NONE);
                allSeries.setTitle(sheet.getRow(2).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),3,0,true,true));

                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line3);
                allSeries.setMarkerStyle(MarkerStyle.NONE);
                allSeries.setTitle(sheet.getRow(3).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),4,0,true,true));

                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line4);
                allSeries.setMarkerStyle(MarkerStyle.NONE);
                allSeries.setTitle(sheet.getRow(4).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),5,0,true,true));

                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line5);
                allSeries.setMarkerStyle(MarkerStyle.NONE);
                allSeries.setTitle(sheet.getRow(5).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),6,0,true,true));

                allSeries= (XDDFLineChartData.Series) chartData.addSeries(dataGenerations,line6);
                allSeries.setMarkerStyle(MarkerStyle.NONE);
                allSeries.setTitle(sheet.getRow(6).getCell(0).getStringCellValue(),new CellReference(sheet.getSheetName(),7,0,true,true));


                try {
                    chart.plot(chartData);
                }catch (NullPointerException e){
                    e.printStackTrace();
                    System.out.println("Error Drawing Chart. File recovery is still an option");
                }

                //Stats table -------
                int statsTableStartRow = numberOfLines+20;
                sheet.getRow(statsTableStartRow).createCell(1).setCellValue("Average Number of Generations Behind");
                sheet.addMergedRegion(new CellRangeAddress(statsTableStartRow,statsTableStartRow+1,1,3));
                sheet.getRow(statsTableStartRow+2).createCell(0).setCellValue("Line");
                sheet.getRow(statsTableStartRow+2).createCell(1).setCellValue("Mean");
                sheet.getRow(statsTableStartRow+2).createCell(2).setCellValue("Stdev.");
                sheet.getRow(statsTableStartRow+2).createCell(3).setCellValue("Median");

                //data
                ArrayList<LineObject> lines = MainStage.settings.lines;
                int dataStartRow = statsTableStartRow+3;
                int lineIndex = 0;
                for(LineObject line : lines){
                    double[] stats = MainStage.entries.generationsBehindStats(line.lineStartNumber,line.lineEndNumber,experimentalGeneration,false);
                    sheet.createRow(dataStartRow+lineIndex).createCell(0).setCellValue(""+line.lineName);
                    sheet.getRow(dataStartRow+lineIndex).createCell(1).setCellValue(stats[0]);
                    sheet.getRow(dataStartRow+lineIndex).createCell(2).setCellValue(stats[1]);
                    sheet.getRow(dataStartRow+lineIndex).createCell(3).setCellValue(stats[2]);

                    lineIndex++;
                }

                //write file
                try{
                    OutputStream outputStream = new FileOutputStream(statsFile);
                    wb.write(outputStream);
                    outputStream.close();
                    wb.close();
                    Desktop.getDesktop().open(statsFile);
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        thread.start();

    }

    private static boolean writeWorkbookFile(XSSFWorkbook workbook, File file){
       boolean fileWritten=false;
       int triesLeft = 10;
       while(fileWritten!=true){
           try{
               OutputStream outputStream = new FileOutputStream(file);
               workbook.write(outputStream);
               outputStream.close();
               workbook.close();
               fileWritten=true;
               Desktop.getDesktop().open(file);
           }catch (FileNotFoundException e) {
               e.printStackTrace();
               System.out.println("Cause of failure is: "+e.getCause().toString());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

        return triesLeft != 0;
    }

    private static void solidLineSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);
    }
}
