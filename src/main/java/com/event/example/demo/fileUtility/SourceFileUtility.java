package com.event.example.demo.fileUtility;

import java.io.File;
import java.io.IOException;

public class SourceFileUtility {


    public File getInputFileObj() {
        String path = "src/main/resources";
        File file = new File(path);
        return new File(file.getAbsolutePath() + "/file.txt");
    }

}
