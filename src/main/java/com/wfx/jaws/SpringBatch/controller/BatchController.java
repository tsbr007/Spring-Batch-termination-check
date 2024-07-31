package com.wfx.jaws.SpringBatch.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    
    @GetMapping("/{jobName}")
    public ResponseEntity<String> getJobStatus(@PathVariable String jobName) {
        Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);

        if (jobExecutions.isEmpty()) {
            return ResponseEntity.ok("No running job found with name: " + jobName);
        }

        StringBuilder statusBuilder = new StringBuilder();
        for (JobExecution jobExecution : jobExecutions) {
            Long executionId = jobExecution.getId();
            BatchStatus status = jobExecution.getStatus();
            ExitStatus exitStatus = jobExecution.getExitStatus();
            statusBuilder.append("Job Execution ID: ").append(executionId)
                    .append(", Status: ").append(status)
                    .append(", Exit Status: ").append(exitStatus)
                    .append("\n");
        }

        return ResponseEntity.ok(statusBuilder.toString());
    }

    @GetMapping("/execution/{executionId}")
    public ResponseEntity<String> getJobExecutionStatus(@PathVariable Long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);

        if (jobExecution == null) {
            return ResponseEntity.ok("No job execution found with ID: " + executionId);
        }

        BatchStatus status = jobExecution.getStatus();
        ExitStatus exitStatus = jobExecution.getExitStatus();
        return ResponseEntity.ok("Job Execution ID: " + executionId
                + ", Status: " + status
                + ", Exit Status: " + exitStatus);
    }
}

