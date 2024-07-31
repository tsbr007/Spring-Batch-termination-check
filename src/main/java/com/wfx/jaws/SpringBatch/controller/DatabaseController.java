package com.wfx.jaws.SpringBatch.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wfx.jaws.SpringBatch.service.DatabaseService;

@RestController
@RequestMapping("/db")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/check")
    public ResponseEntity<String> checkDatabase() {
        try {
            String result = databaseService.checkDatabase();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database check failed: " + e.getMessage());
        }
    }
}
