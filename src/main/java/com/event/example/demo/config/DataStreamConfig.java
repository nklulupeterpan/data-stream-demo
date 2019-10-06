package com.event.example.demo.config;

import javax.sql.DataSource;

import com.event.example.demo.fileUtility.SourceFileUtility;
import com.event.example.demo.services.DataStreamFromTxtService;
import com.event.example.demo.services.GenerateInputService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class DataStreamConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("classpath:data-schema.sql").build();
    }

    @Bean
    public GenerateInputService generateInput() {
        return new GenerateInputService();
    }

    @Bean
    public SourceFileUtility sourceFileUtility() {
        return new SourceFileUtility();
    }

    @Bean
    public DataStreamFromTxtService dataStreamFromTxt() {
        return new DataStreamFromTxtService();
    }
}
