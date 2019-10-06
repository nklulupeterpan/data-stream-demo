package com.event.example.demo.resources;

import com.event.example.demo.services.GenerateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Description(value = "Data Stream Handler")
@RestController
@RequestMapping("/api/")
public class DataStreamResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamResource.class);

    @Autowired
    private GenerateInput generateInput;


    /**
     * Endpoint for generating simple PDF report
     *
     * @return HTTP code and string
     */
    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generatePDFReport() throws IOException {
        LOGGER.info("Generating Input file.txt...");

        boolean success =false;

        success = generateInput.writeInputData();

        if (success) {
            return new ResponseEntity<>("The Input File Successfully Generated ", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Fail to generate the input File ", HttpStatus.OK);
        }
    }
}
