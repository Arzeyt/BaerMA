package com.BaerMA;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class JGitMain {



    public JGitMain() {
        initialize();
    }

    public void initialize(){
        String gitEntriesDir = MainStage.settings.dataDirectory+"/Git";
        try {
            Repository existingRepo = new FileRepositoryBuilder()
                    .setGitDir(new File(gitEntriesDir))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
