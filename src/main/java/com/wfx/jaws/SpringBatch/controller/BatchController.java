package com.wfx.jaws.SpringBatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @PostMapping("/start")
    public ResponseEntity<Long> startJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
           JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok(jobExecution.getId());
        } catch (Exception e) {
        	
        	e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopJob() {
        try {
            Set<JobExecution> runningExecutions = new HashSet<>(jobExplorer.findRunningJobExecutions("job"));
            if(runningExecutions.isEmpty()) {
                return ResponseEntity.status(400).body("No Running Jobs ");

            }
            for (JobExecution jobExecution : runningExecutions) {
                jobOperator.stop(jobExecution.getId());
            }
            return ResponseEntity.ok("Stopped all running job instances");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to stop jobs: " + e.getMessage());
        }
    }
}

