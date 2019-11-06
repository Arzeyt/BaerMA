package com.BaerMA;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GoogleCloud {

    StorageOptions options = null;
    Storage storage = null;
    String PROJECT_ID = null;
    String PATH_TO_JSON_KEY = null;
    String BUCKET_NAME = null;
    String ENTRIES_FILE_NAME = null;
    File cloudEntries = null;

    public GoogleCloud(){

    }

    public void initialize() {
        PROJECT_ID = "BaerMACloud";
        PATH_TO_JSON_KEY = MainStage.settings.dataDirectory+ File.separator+"BearMACloud-ac3a443978cb.json";
        BUCKET_NAME = "bma-entries-data";
        ENTRIES_FILE_NAME = "Entries.json";

        //create StorageOptions (gives access to the project
        try {
            options = StorageOptions.newBuilder()
                    .setProjectId(PROJECT_ID)
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(PATH_TO_JSON_KEY))).build();
            storage = options.getService();
            Blob blob = storage.get(BUCKET_NAME, ENTRIES_FILE_NAME);
            ReadChannel r = blob.reader();
            String cloudEntriesString = MainStage.settings.dataDirectory+"/CloudEntries.json";
            Path cloudEntriesPath = Paths.get(cloudEntriesString);
            blob.downloadTo(cloudEntriesPath);
            cloudEntries= new File(cloudEntriesString);
            if(cloudEntries.exists()){
                System.out.println("Created a local copy of Entries.json");
                MainStage.alertInfo("Info","Loaded Entries.json from the cloud","");
            }else{
                System.out.println("Could not create a local copy of Entries.json");
                MainStage.alertError("Error","Could not load Entries.json from the cloud","This program will launch using local backups");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MainStage.alertError("Error","Could not connect to cloud storage","Ensure .json key exists and internet is connected");
        }



    }

    public void uploadEntries(){

    }


}
