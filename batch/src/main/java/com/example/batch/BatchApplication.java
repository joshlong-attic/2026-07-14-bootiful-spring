package com.example.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;


record Customer(int id, String name) {
}

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Bean
    JdbcBatchItemWriter<Customer> customerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("insert into customer(name) values (:name)")
                .beanMapped()
                .build();
    }

    @Bean
    FlatFileItemReader<Customer> customerReader(@Value("classpath:data.csv") Resource csv) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerReader")
                .resource(csv)
                .linesToSkip(1)
                .delimited(s -> s.names("id", "name"))
                .targetType(Customer.class)
//                .fieldSetMapper(fieldSet -> new Customer(fieldSet.readInt("id"), fieldSet.readString("name")))
                .build();
    }

    @Bean
    Step reset(JobRepository repository, JdbcClient jdbcClient) {
        return new StepBuilder(repository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcClient.sql("delete from customer").update();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    VirtualThreadTaskExecutor virtualThreadTaskExecutor (){
        return new VirtualThreadTaskExecutor();
    }

    @Bean
    Step etl(JobRepository repository,
             VirtualThreadTaskExecutor virtualThreadTaskExecutor ,
             FlatFileItemReader<Customer> customerReader,
             JdbcBatchItemWriter<Customer> customerWriter
    ) {
        return new StepBuilder(repository)
                .<Customer, Customer>chunk(10)
                .reader(customerReader)
                .writer(customerWriter)
                .taskExecutor(virtualThreadTaskExecutor)
                //.writer(chunk -> chunk.forEach(IO::println))
                .build();
    }

    @Bean
    Job job(JobRepository repository, Step reset, Step etl) {
        return new JobBuilder(repository)
                .incrementer(new RunIdIncrementer())
                .start(reset)
                .next(etl)
                .build();
    }

    // Job
    // > 0..N Steps
    // > Step (Tasklet || (ItemReader, ItemProcessor, ItemWriter))
}

