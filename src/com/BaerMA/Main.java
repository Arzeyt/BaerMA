package com.BaerMA;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Main extends Application{

    //Entries list and reader
    public static HashSet<Entry> entries = new HashSet<Entry>();
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    //terminal variables
    public static DateObj currentDate = null;
    public static int currentGeneration = 0;

    public static String chelp="help",cnew = "new", clist="list", cserialize="serialize", cclear="clear entries", cedit="edit";


    //GUI
    public static MainStage mainStage = new MainStage();

    public static void main(String[] args) throws IOException {

        //System.out.println("Deserializing entries");
        //deserializeEntries();

        dividingFlair();
        help();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        mainStage.start(primaryStage);

        //terminalLoop();
    }

    /**
    public static void newEntry() throws IOException {
        boolean loop = true;
        while(loop) {
            if(currentDate==null) changeDate();
            if(currentGeneration==0) changeGeneration();
            dividingFlair();
            System.out.println("Generation to edit: "+currentGeneration+"    Date to edit: "+currentDate);
            System.out.println("Type 'date' to change date and 'generation' to change experimental generation");
            System.out.println("Type the <sample number> <backup generation> <month> <day> <year> and <notes>(optional)");

            String s = reader.readLine();

            if (s.equals("date")) {
                changeDate();
            } else if (s.equals("generation")) {
                changeGeneration();
            } else if (s.equals("exit") || s.equals("return") || s.equals("back") || s.equals("back")){
                return;
            }else if(isInteger(s.split(" ")[0])){

                String[] split = s.split(" ");

                if (split.length < 1) {
                    System.out.println("You're missing some info there");
                } else {
                    try {
                        System.out.println("You entered: " + s + "\n Parsing as: ");
                        for (String i : split) {
                            System.out.println(i);
                        }
                        int sampleNumber = Integer.parseInt(split[0]);
                        int backupGeneration = Integer.parseInt(split[1]);
                        int bmonth = Integer.parseInt(split[2]);
                        int bday = Integer.parseInt(split[3]);
                        int byear = Integer.parseInt(split[4]);

                        String notes = "";
                        System.out.println("split length: "+split.length);
                        if (split.length > 5) {
                            System.out.println("parsing notes");
                            for(int i =5;i<split.length;i++){
                                notes = notes.concat(" "+split[i]);
                                System.out.println(notes);
                            }

                        }


                        Entry entry = new Entry(sampleNumber, currentGeneration, currentDate.year, currentDate.month, currentDate.day, backupGeneration, byear,bmonth,bday,notes);

                        System.out.println("Created new entry object: \n" + entry);

                        if (entryExists(sampleNumber, currentGeneration)) {
                            System.out.println("There is already an entry with this id in this experimental generation! Edit coming soon");
                            return;
                        }
                        entries.add(entry);
                        System.out.println("Added entry to list");


                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("there was an error parsing the sample number and backup generation");
                    }
                }
            }else{
                System.out.println("unrecognized command");
            }
        }
    }
     */

    public static void changeDate() throws IOException {
        System.out.println("Please enter the date in this format: Month Day Year");
        String[] date = reader.readLine().split(" ");
        currentDate = new DateObj(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        System.out.println("Date set to: "+currentDate.month+" "+currentDate.day+" "+currentDate.year);
    }

    public static void changeGeneration() throws IOException{
        System.out.println("Please enter the experimental generation to edit: ");
        currentGeneration=Integer.parseInt(reader.readLine());
        System.out.println("Experimental generation set to: "+currentGeneration);
    }

    public static void dividingFlair(){
        System.out.println("----------------------------");
        return;
    }

    public static void help(){
        System.out.println(
                "new            make a new entry \n" +
                "edit           edit an entry \n" +
                "list           list all entries \n"+
                "serialize      serialize (save) data \n"+
                "clear entries  clear entries \n" +
                "help           list commands"
                );
    }

    public static boolean entryExists(int id, int experimentalGeneration){
        for(Entry e :entries){
            if(e.equalsID(id)&&e.experimentalGeneration ==experimentalGeneration){
                return true;
            }
        }
        return false;
    }


    public static void serializeEntries(){
        try
        {
            File saveFile = new File("entries");
            String filePath=saveFile.getAbsolutePath();

            //Saving of object in a file
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);

            // Method for serialization of object
            outputStream.writeObject(entries);

            outputStream.close();
            fileOutputStream.close();

            System.out.println("entries have been serialized");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
    }

    public static void deserializeEntries(){
        if(new File("entries").exists()){
            System.out.println("entries exists");
        }else{
            System.out.println("entries does not exist");
            return;
        }
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream("entries");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            HashSet<Entry> e = (HashSet<Entry>) in.readObject();
            entries=e;

            in.close();
            file.close();

            System.out.println(e.size()+" entries have been deserialized ");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    public static void clearEntries(){
        entries.clear();
        System.out.println("Entries cleared");
    }

    /**  Disabled this code due to deprecated storage of dates. Not worth fixing if migrating to javaFX
    public static void editEntries() throws IOException {
        int id = 0, mainGeneration = 0;
        boolean loop=true;

        while(loop=true) {
            System.out.println("Enter id and experimental generation of entry to edit or 'back' to return to the menu");
            String s = reader.readLine();
            String[] split = s.split(" ");
            if(s.equals("back")){
                System.out.println("returning to menu");
                return;
            }else if(isInteger(split[0])&&isInteger(split[1])){
                id = Integer.parseInt(split[0]);
                mainGeneration = Integer.parseInt(split[1]);
                HashSet<Entry> matchingEntries = new HashSet<Entry>();

                for (Entry e : entries) {
                    if (e.id == id && e.experimentalGeneration ==mainGeneration) matchingEntries.add(e);
                }
                if (matchingEntries.size() < 1) {
                    System.out.println("no entries have been made for this id");
                }else if(matchingEntries.size()>1){
                    System.out.println("List of all entries that match the given id: ");
                    dividingFlair();
                    for(Entry e : matchingEntries){
                        System.out.println(e);
                    }
                }else if(matchingEntries.size()==1){
                    for(Entry e: matchingEntries){
                        System.out.println("Found entry to edit: \n"+e+"\n Enter <sample number> <experimental generation> <backup Generation> <month> <day> <year>and <notes>(optional) to replace this entry");
                        s=reader.readLine();
                        if(s.equals("back")){
                            System.out.println("returning to menu");
                            return;
                        }
                        split = s.split(" ");
                        if(split.length<7){
                            System.out.println("not enough arguments");
                        }else{
                            e.id=Integer.parseInt(split[0]);
                            e.experimentalGeneration =Integer.parseInt(split[1]);
                            e.backupGeneration=Integer.parseInt(split[2]);
                            e.bmonth=Integer.parseInt(split[3]);
                            e.bday=Integer.parseInt(split[4]);
                            e.byear=Integer.parseInt(split[5]);

                            if(split.length>=6){
                                String notes = "";

                                for(int i =6;i<split.length;i++){
                                    notes=notes.concat(" "+split[i]);
                                }
                                e.notes=notes;

                            }
                            dividingFlair();
                            System.out.println("New entry: \n"+e);
                            dividingFlair();
                        }
                    }


                }
            }else{
                System.out.println("Not sure what that means... try again");
            }
        }

    }
     */

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static void listSampleHistory(int sampleID){
        ArrayList<Entry> history = new ArrayList<Entry>();
        for(Entry e : entries){
            if(e.equalsID(sampleID)){
                history.add(e);
            }
        }
        System.out.println("history contains "+history.size()+" elements");

        history.sort(new SortByDate());
        for(Entry e:history){
           // System.out.println("\nOn "+e.getDate()+" this sample was backed up from generation "+e.backupGeneration+" of "+e.getBackupOfDate()+
                 //   "\nNotes: "+e.notes);
        }
    }

    public static Integer calculateGeneration(int sampleID, int experimentalGeneration){
        ArrayList<Entry> history = new ArrayList<Entry>();
        for(Entry e : entries){
            if(e.equalsID(sampleID)){
                history.add(e);
            }
        }
        System.out.println("history contains "+history.size()+" elements");

        history.sort(new SortByDate());
        Collections.sort(history,Collections.reverseOrder());

        Entry e = history.get(0); //most recent entry
        int currentGeneration = e.backupGeneration + (experimentalGeneration-e.experimentalGeneration);
        return currentGeneration;
    }



    private void terminalLoop() throws IOException {
        boolean loop = true;
        do {
            dividingFlair();

            String s = reader.readLine();

            if (s.equals(cnew)) {
                dividingFlair();
                //newEntry();
            }else if(s.equals(chelp)){
                dividingFlair();
                help();
                dividingFlair();
            } if(s.contains(clist)){
                if(s.equals("list")) {
                    System.out.println("list long or list short?");
                    s="list "+reader.readLine();
                }
                if(s.equals("list short")){ //list all short
                    for(Entry e:entries){
                        System.out.println("id: "+e.id+"    experimental generation: "+e.experimentalGeneration +"    backup generation: "+e.backupGeneration);
                    }
                }else if(s.equals("list long")) { //list all long
                    dividingFlair();
                    System.out.println("Listing your entries: ");
                    for (Entry e : entries) {
                        System.out.print(e + "\n");
                    }
                }else if(s.contains("list history")){
                    String[] split = s.split(" ");
                    if(split.length<3){
                        System.out.println("enter sample number");
                        s=reader.readLine();
                        if(isInteger(s)){
                            listSampleHistory(Integer.parseInt(s));
                        }
                    }else if(split.length==3){
                        if(isInteger(split[2])){
                            listSampleHistory(Integer.parseInt(split[2]));
                        }else{
                            System.out.println("not a valid input");
                        }
                    }else{
                        System.out.println("not a valid input");
                    }
                }
            }else if(s.equals(cserialize)||s.equals("save")){
                dividingFlair();
                serializeEntries();
            }else if(s.equals(cclear)){
                dividingFlair();
                clearEntries();
            }else if(s.equals(cedit)){
                dividingFlair();
                //editEntries();
            }else if(s.contains("calculate")){
                String[] split = s.split(" ");
                System.out.println(calculateGeneration(Integer.parseInt(split[1]) ,Integer.parseInt(split[2]) ));
            }else{
                System.out.println("Unrecognized Command. Type 'help' for command list");
            }


        } while (loop);

    }
}