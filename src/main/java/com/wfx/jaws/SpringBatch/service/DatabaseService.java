package com.wfx.jaws.SpringBatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String checkDatabase() {
        try {
            // Check if the table exists
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BATCH_JOB_INSTANCE'");

            if (tables.isEmpty()) {
                return "Table BATCH_JOB_INSTANCE does not exist.";
            }

            // Query the content of the BATCH_JOB_INSTANCE table
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM BATCH_JOB_INSTANCE");

            if (rows.isEmpty()) {
                return "Table BATCH_JOB_INSTANCE is empty.";
            }

            StringBuilder result = new StringBuilder("Table BATCH_JOB_INSTANCE content:\n");
            for (Map<String, Object> row : rows) {
                result.append(row).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException("Database check failed", e);
        }
    }
}

