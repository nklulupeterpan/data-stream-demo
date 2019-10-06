package com.event.example.demo.model;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "data")
public class Event implements Serializable {

    @Id
    @Getter
    @Setter
    private String id;


    @Getter
    @Setter
    private Integer duration;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private Boolean alert;

}
