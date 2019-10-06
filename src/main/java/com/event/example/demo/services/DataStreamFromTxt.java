package com.event.example.demo.services;

import com.event.example.demo.resources.DataStreamResource;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DataStreamFromTxt {

//
//    @Autowired
//    SourceFileUtility sourceFileUtility;

//    @Autowired
//    private JsonParseRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamResource.class);

    private final Integer MAX_LINES = 20;
    private File file;
    private BufferedReader bufferedReader;
    private int lineCount = 0;
    private int readBufferSize = 11;
    private ReentrantLock readlock = new ReentrantLock();
    private List<JSONArray> readBuffered = new LinkedList<>();
    boolean finishreading = false;
    private JSONParser jsonParser = new JSONParser();
    private HashMap<String, >


    public static void main(String[] args) {
        DataStreamFromTxt test = new DataStreamFromTxt();
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    test.readFileManager();
                } catch (Exception e) {
                    throw new RuntimeException("Exception Rasied in this thread", e);
                }
            }
        });

    }

    void initLoacalVariable() throws FileNotFoundException {
        file = getInputFileObj();
        bufferedReader = new BufferedReader(new FileReader(file));
    }

//   public void startDataStreaming() throws FileNotFoundException {
//        initLoacalVariable();
//        if (!file.exists()){
//            throw new RuntimeException("the input file does not exist");
//        }


//    }

    void readFile() throws IOException, ParseException {
        JSONArray toReturn = new JSONArray();
        int endLineCount = lineCount + MAX_LINES;
        LOGGER.info("reading from {} to {}", lineCount, endLineCount);

        while (lineCount < endLineCount) {
            LOGGER.info("reading {}", lineCount);
            String readLine = bufferedReader.readLine();
            LOGGER.info("line content {}", readLine);
            if (readLine != null) {
                try {
                    Object obj = jsonParser.parse(readLine);
                    toReturn.add(obj);
                    lineCount++;
                } catch (ParseException e) {
                    LOGGER.error("Exception occurs on line {}", lineCount, e);
                    if (!toReturn.isEmpty()) {
                        readBuffered.add(toReturn);
                    }
                    throw e;
                }

            } else {
                finishreading = true;
                bufferedReader.close();
                if (!toReturn.isEmpty()) {
                    readBuffered.add(toReturn);
                }
                LOGGER.info("I am retruned ----------------------");
                return;
            }
        }
        if (!toReturn.isEmpty()) {
            LOGGER.info("This back read is {}", toReturn);
            readBuffered.add(toReturn);
        }

    }

    void readFileManager() throws IOException, ParseException, InterruptedException {

        initLoacalVariable();
                if (!file.exists()){
            throw new RuntimeException("the input file does not exist");
        }

        while (!finishreading) {
            LOGGER.info("-------------------- started");
            LOGGER.info("Count of buffer {}", readBuffered.size());
            LOGGER.info("finishreading {}", finishreading);
            readlock.lock();

            if (readBuffered.size() < readBufferSize) {
                readFile();
                LOGGER.info("-------------------- FINISHED");

            } else {
                Thread.sleep(3000);
                LOGGER.info("-------------------- FINISHED");

            }
            readlock.unlock();

        }
        LOGGER.info("-------------------- full stoped");
    }

    public File getInputFileObj() {
        String path = "src/main/resources";
        File file = new File(path);
        return new File(file.getAbsolutePath() + "/file.txt");
    }


//    void
//            //Read JSON file
//            Object obj = jsonParser.parse(reader);
//
//            JSONArray employeeList = (JSONArray) obj;
//
//            //Iterate over employee array
//            employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );
//
//    }
}

