package com.event.example.demo.model;

import jdk.jfr.Description;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Description(value = "this model is used to aggregate start and finis event")
public class JsonPaser {

    @Getter
    @Setter
    private Long startTime;

    @Getter
    @Setter
    private Long finishTime;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String host;

}
