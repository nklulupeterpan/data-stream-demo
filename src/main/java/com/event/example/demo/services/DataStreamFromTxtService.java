package com.event.example.demo.services;

import com.event.example.demo.fileUtility.SourceFileUtility;
import com.event.example.demo.model.Event;
import com.event.example.demo.repository.EventRepository;
import com.event.example.demo.resources.DataStreamResource;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class DataStreamFromTxtService {

    @Autowired
    private SourceFileUtility sourceFileUtility;

    @Autowired
    private EventRepository eventRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamResource.class);

    private final Integer MAX_LINES = 13;
    private final Integer MAX_DURATION = 4;

    private final String DURATION  =  "duration";

    private File file;
    private BufferedReader bufferedReader;
    private Integer lineCount = 0;
    private Integer lastLine = 0;
    private Integer currentLineWrite = 0;
    private int readBufferSize = 3;
    private ReentrantLock readlock = new ReentrantLock();
    private ReentrantLock writelock = new ReentrantLock();
    private final int SLEEP = 300;

    private  List<JSONArray> readBuffered = Collections.synchronizedList(new ArrayList<JSONArray>());
    boolean finishreading = false;

    private JSONParser jsonParser = new JSONParser();
    private HashMap<String, JSONObject> unpairedEvent = new HashMap<>();


    public  boolean dataStreaming() throws FileNotFoundException {

        initBufferedReader();

        if (!file.exists()){
            throw new RuntimeException("the input file does not exist");
        }
        // Start file reader manager
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    readFileManager();
                } catch (Exception e) {
                    throw new RuntimeException("Exception raised in this thread", e);
                }
            }
        });

        // Start DB writer manager
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    writeDBManager();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Exception raised in this thread", e);
                }
            }
        });

        //Wait for both managers finish
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    void initBufferedReader() throws FileNotFoundException {
        file = sourceFileUtility.getInputFileObj();
        bufferedReader = new BufferedReader(new FileReader(file));
    }

    void readFile() throws IOException, ParseException {
        JSONArray toReturn = new JSONArray();
        int endLineCount = lineCount + MAX_LINES;
        LOGGER.info("reading from {} to {}", lineCount, endLineCount);

        while (lineCount < endLineCount) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                try {
                    JSONObject obj = (JSONObject) jsonParser.parse(readLine);
                    obj.put("lineCount",lineCount);
                    LOGGER.debug("The JSON transformed for line {} is {}", lineCount, obj);
                    toReturn.add(obj);
                    lineCount++;
                } catch (ParseException e) {
                    if (!toReturn.isEmpty()) {
                        readBuffered.add(toReturn);
                    }
                    throw e;
                }
            } else {
                lastLine = lineCount-1;
                LOGGER.info("Finish reading input file, total number of lines is {}", lastLine );
                finishreading = true;
                // Close reader after reading finished
                bufferedReader.close();
                if (!toReturn.isEmpty()) {
                    readBuffered.add(toReturn);
                }
                return;
            }
        }
        if (!toReturn.isEmpty()) {
            readBuffered.add(toReturn);
        }
    }

    void readFileManager() throws IOException, ParseException, InterruptedException {

        while (!finishreading) {
            LOGGER.info("start to read file");
            readlock.lock();
            if (readBuffered.size() < readBufferSize) {
                readFile();
            } else {
                Thread.sleep(SLEEP);
            }
            readlock.unlock();
        }
        LOGGER.info("the execution of readManager finishes");
    }

    List<Event> aggregateJson(JSONArray inputArray) {
        List<Event> toReturn = new ArrayList<>();
        for (Object item: inputArray) {
            JSONObject jsonNewFromRead = (JSONObject) item;
            JSONObject jsonInUnpaired = unpairedEvent.get(jsonNewFromRead.get("id"));
            if (jsonInUnpaired != null){
                // Merge two JSONs refers to start and finish together. in case type and host only in one of them.
                JSONObject jsonWithDuration =  jsonMerger(jsonNewFromRead,jsonInUnpaired);
                LOGGER.debug("The JSON to write {}", jsonWithDuration);
                toReturn.add( jsonToEventAdapter(jsonWithDuration));
                unpairedEvent.remove(jsonWithDuration.get("id"));
                currentLineWrite = (Integer) jsonNewFromRead.get("lineCount");
            }
            else {
                unpairedEvent.put((String) jsonNewFromRead.get("id"), jsonNewFromRead);
                currentLineWrite = (Integer) jsonNewFromRead.get("lineCount");
            }
        }
        return toReturn;
    }

    boolean checkWriteFinished() {
        LOGGER.debug("Writer has reached line {}", currentLineWrite );
        LOGGER.debug("The reading finish status {}",finishreading );
        if (finishreading && lastLine.equals(currentLineWrite)){
            return  true;
        }
        else{
            return false;
        }
    }
    JSONObject jsonMerger(JSONObject object1, JSONObject object2){
        JSONObject jsonMerge = new JSONObject();
        //Calculate the duration
        int duration = (int) Math.abs((long) object1.get("timestamp") - (long) object2.get("timestamp"));
        List<JSONObject> inputList = Arrays.asList(object1, object2);
        for (JSONObject jsonObj : inputList ){
            Set<Map.Entry> jsonEntrySet = jsonObj.entrySet();
            for ( Map.Entry<String, Object> jsonEntry: jsonEntrySet){
                jsonMerge.put(jsonEntry.getKey(),jsonEntry.getValue());
            }
         }
        jsonMerge.put(DURATION, duration);
        return jsonMerge;
    }

    Event jsonToEventAdapter(JSONObject jsonIn){
        Event toReturn = new Event();
        toReturn.setId((String) jsonIn.get("id"));
        if ( jsonIn.get(DURATION)!= null){
            Integer duration = (Integer) jsonIn.get(DURATION);
            toReturn.setDuration(duration);
            if (duration > MAX_DURATION){
                toReturn.setAlert(true);
            }
            else{
                toReturn.setAlert(false);
            }
        }
        if (jsonIn.get("host") != null){
            toReturn.setHost((String) jsonIn.get("host"));
        }
        if (jsonIn.get("type") != null){
            toReturn.setType((String) jsonIn.get("type"));
        }

        return toReturn;
    }

    void writeToDb(List<Event> inputList){
        for (Event item: inputList){
            eventRepository.save(item);
        }
    }
    void writeDBManager() throws InterruptedException {
        while (true){
            writelock.lock();
            if(checkWriteFinished()){
                List<Event> eventToWrite = new ArrayList<>();
                for (Object item: (unpairedEvent.values())){
                   JSONObject unPairedJsonToWrite = (JSONObject) item;
                    eventToWrite.add(jsonToEventAdapter(unPairedJsonToWrite)) ;
                }
               writeToDb(eventToWrite);
                LOGGER.info("the execution of writeManager finishes");
                return;
            }

            if (!readBuffered.isEmpty())
            {
                JSONArray firstArray = readBuffered.get(0);
                JSONObject firstElement = (JSONObject) firstArray.get(0);
                JSONObject lastElement = (JSONObject) firstArray.get(firstArray.size()-1);

                readBuffered.remove(0);
                LOGGER.info("About to process line from {} to {}", firstElement.get("lineCount"),  lastElement.get("lineCount"));
                List<Event> toWrite = aggregateJson(firstArray);
                writeToDb(toWrite);
                writelock.unlock();
            }
            else{
                Thread.sleep(SLEEP);
                writelock.unlock();
            }

        }

        }
}

