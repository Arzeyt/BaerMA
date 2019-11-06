package com.BaerMA;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class GoogleCloud {

    public GoogleCloud(){

    }

    public void initialize() {
        String PROJECT_ID = "BaerMACloud";
        String PATH_TO_JSON_KEY = "/path/to/json/key";
        String BUCKET_NAME = "my-bucket";
        String OBJECT_NAME = "my-object";

        StorageOptions options = null;
        try {
            options = StorageOptions.newBuilder()
                    .setProjectId(PROJECT_ID)
                    .setCredentials(GoogleCredentials.fromStream(
                            new FileInputStream(PATH_TO_JSON_KEY))).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Storage storage = options.getService();
        Blob blob = storage.get(BUCKET_NAME, OBJECT_NAME);
        ReadChannel r = blob.reader();
    }
}
