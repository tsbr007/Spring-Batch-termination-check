package com.wfx.jaws.SpringBatch.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Configuration
public class SchemaInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeSchema() {
        try {
            String sql = Files.lines(Paths.get("src/main/resources/schema-h2.sql"))
                              .collect(Collectors.joining(" "));
            jdbcTemplate.execute(sql);
            System.out.println("Schema initialized successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }
}

