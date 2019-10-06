package com.event.example.demo.resources;

import com.event.example.demo.services.DataStreamFromTxtService;
import com.event.example.demo.services.GenerateInputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;

@Description(value = "Data Stream Handler")
@RestController
@RequestMapping("/api/")
public class DataStreamResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamResource.class);

    @Autowired
    private GenerateInputService generateInputService;
    @Autowired
    private DataStreamFromTxtService dataStreamFromTxtService;


    /**
     * Endpoint for generating input file in /resources/file.txt
     *
     * @return HTTP code and string
     */
    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateInput() throws IOException {
        LOGGER.info("Generating Input file.txt...");

        boolean success;

        success = generateInputService.writeInputData();

        if (success) {
            return new ResponseEntity<>("The Input File Successfully Generated ", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Fail to generate the input File ", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> streamToDB() throws FileNotFoundException {
        LOGGER.info("Streaming...");
        boolean success;
        success = dataStreamFromTxtService.dataStreaming();

        if (success) {
            return new ResponseEntity<>("The streaming finished ", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("FAIL to Streaming", HttpStatus.OK);
        }
    }


}
