package com.event.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Description;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Description(value = "fund Model Definition")
public class Fund implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;


    @Getter
    @Setter
    private String fundCode;


    @Getter
    @Setter
    private String fundName;

    @Getter
    @Setter
    private String bank;
}
