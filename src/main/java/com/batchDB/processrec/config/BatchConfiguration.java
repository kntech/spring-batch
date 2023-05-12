package com.batchDB.processrec.config;

import com.batchDB.processrec.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public ItemReader<Employee> itemReader() throws UnexpectedInputException {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/employees.csv"));
        reader.setName("EmployeeDetailsReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(getLineMapper());
        return reader;
    }

    private LineMapper<Employee> getLineMapper() {
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();

        //Custom FieldSet Mapper
        lineMapper.setFieldSetMapper(fs -> {
            Employee employee = new Employee();
            employee.setName(fs.readString("name"));
            employee.setAddress(fs.readString("address"));
            employee.setDateofbirth(fs.readString("dateofbirth"));

            if (fs.readString("hobbies") != null) {
                String[] hobbies = fs.readString("hobbies").split(":");
                employee.setHobbies(List.of(hobbies));
            } else {
                employee.setHobbies(Collections.emptyList());
            }

            return employee;
        });

        //Tokenizer
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = {"name", "address", "dateofbirth", "hobbies"};
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames(tokens);
        lineMapper.setLineTokenizer(tokenizer);
        return lineMapper;
    }

    @Bean
    protected Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Employee> itemReader, ItemProcessor<Employee, Employee> itemProcessor, ItemWriter<Employee> itemWriter) {
        return new StepBuilder("step1", jobRepository).<Employee, Employee>chunk(10, transactionManager).reader(itemReader).processor(itemProcessor).writer(itemWriter).build();
    }

    @Bean(name = "employeeBatchJob")
    public Job job(JobRepository jobRepository, @Qualifier("step1") Step step1) {
        return new JobBuilder("employeeBatchJob", jobRepository).preventRestart().start(step1).build();
    }

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql").addScript("classpath:org/springframework/batch/core/schema-h2.sql").build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean(name = "jobRepository")
    public JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "jobLauncher")
    public JobLauncher getJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}