package com.batchDB.processrec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class EmployeeBatchApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeBatchApplication.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job employeeBatchJob;

    public static void main(String[] args) {
        SpringApplication.run(EmployeeBatchApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            JobParametersBuilder paramsBuilder = new JobParametersBuilder();
            paramsBuilder.addDate("Batch Initiated Date", new Date());
            JobExecution jobExecution = jobLauncher.run(employeeBatchJob, paramsBuilder.toJobParameters());
            if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

                LOGGER.info("==========JOB FINISHED SUCCESSFULLY=======");
                LOGGER.info("JobId      : {}",jobExecution.getJobId());
                LOGGER.info("JobId      : {}",jobExecution.getJobInstance().getJobName());
                LOGGER.info("Start Date: {}", jobExecution.getCreateTime());
                LOGGER.info("End Date: {}", jobExecution.getEndTime());
                LOGGER.info("============================================");
            }
        };
    }

}
