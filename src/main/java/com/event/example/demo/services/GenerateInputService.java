package com.event.example.demo.services;
//

import java.io.*;
import java.util.Collections;
import java.util.Random;

import com.event.example.demo.fileUtility.SourceFileUtility;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateInputService {

    @Autowired
    SourceFileUtility sourceFileUtility;

    public boolean writeInputData() {
        try {
            GenerateInputService generateInputService = new GenerateInputService();
            File file = sourceFileUtility.getInputFileObj();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            JSONArray jsonArray = generateInputService.generateRandomInput(100, 8);
            generateInputService.writeToFile(fileWriter, jsonArray);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    JSONArray generateRandomInput(int numberLines, int maxSpendMillseconds) {
        JSONArray toWrite = new JSONArray();
        Random random = new Random();
        for (int i = 0; i < numberLines; i++) {
            Long startTime = System.currentTimeMillis();
            Long endTime = startTime + random.nextInt(maxSpendMillseconds - 1) + 1;
            toWrite.add(generateJsonObj(i, "STARTED", startTime));
            toWrite.add(generateJsonObj(i, "FINISHED", endTime));
        }
        Collections.shuffle(toWrite);
        return toWrite;
    }

    void writeToFile(FileWriter fileWriter, JSONArray arrayIn) throws IOException {
        for (Object obj : arrayIn) {
            JSONObject objTrans = (JSONObject) obj;
            fileWriter.write(objTrans.toJSONString() + System.lineSeparator());
        }
    }

    JSONObject generateJsonObj(int idInt, String status, Long timestamp) {
        Random random = new Random();
        JSONObject obj = new JSONObject();
        obj.put("id", "scsmbstgr" + String.format("%010d", idInt));
        obj.put("state", status);
        obj.put("timestamp", timestamp);
        if (random.nextBoolean()) {
            obj.put("type", "APPLICATION_LOG");
            obj.put("host", "12345");
        }
        return obj;
    }


}
