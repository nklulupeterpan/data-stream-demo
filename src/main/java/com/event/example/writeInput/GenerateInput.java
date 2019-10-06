//package com.event.example.writeInput;
//
//import java.io.*;
//import java.net.URL;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.Random;
//
//import ch.qos.logback.core.util.FileUtil;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
//
//public class GenerateInput {
//    public static void main(String[] args) throws IOException {
//        GenerateInput generateInput= new GenerateInput();
//        String resourcePath = generateInput.getResourcePath();
//        File file = new File(resourcePath+"/file.txt");
//        file.createNewFile();
//        FileWriter fileWriter = new FileWriter (file);
//        JSONArray jsonArray =   generateInput.generateRandomInput(100, 8);
//        generateInput.writeToFile(fileWriter, jsonArray);
//        fileWriter.flush();
//        fileWriter.close();
//    }
//
//    JSONArray generateRandomInput( int numberLines, int maxSpendMillseconds){
//       JSONArray toWrite = new JSONArray();
//       Random random = new Random();
//        for (int i = 0; i< numberLines; i++){
//            Long startTime = System.currentTimeMillis();
//            Long endTime = startTime + random.nextInt(maxSpendMillseconds-1) + 1;
//            toWrite.add(generateJsonObj(i,"STARTED", startTime));
//            toWrite.add(generateJsonObj(i,"FINISHED", endTime));
//        }
//        Collections.shuffle(toWrite);
//        return toWrite;
//    }
//
//    void writeToFile(FileWriter fileWriter, JSONArray arrayIn) throws IOException {
//        for ( Object obj :arrayIn){
//            JSONObject objTrans = (JSONObject) obj;
//            fileWriter.write(objTrans.toJSONString()+System.lineSeparator() );
//        }
//    }
//
//
//     JSONObject generateJsonObj(int idInt, String status, Long timestamp){
//        Random random = new Random();
//        JSONObject obj = new JSONObject();
//        obj.put("id", "scsmbstgr"+   String.format("%010d", idInt));
//        obj.put("state",status);
//        obj.put("timestamp",timestamp );
//        if (random.nextBoolean()){
//            obj.put("type", "APPLICATION_LOG");
//            obj.put("host", "12345");
//        }
//        return obj;
//    }
//   String getResourcePath(){
//       String path = "src/main/resources";
//       File file = new File(path);
//       return  file.getAbsolutePath();
//    }
//
//}
