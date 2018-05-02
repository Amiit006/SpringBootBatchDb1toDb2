package com.infotech.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.infotech.batch.model.Person;
import com.infotech.batch.processor.PersonItemProcessor;
import com.infotech.batch.processor.PersonItemProcessor2;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
	public JdbcCursorItemReader<Person> readerStep2(){
		JdbcCursorItemReader<Person> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT person_id,first_name,last_name,email,age FROM person limit 1");
		cursorItemReader.setRowMapper(new PersonRowMapper());
		return cursorItemReader;
	}
    @Bean
    public PersonItemProcessor2 processorStep2() {
        return new PersonItemProcessor2();
    }
    @Bean
    public JdbcBatchItemWriter<Person> writerStep2() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO person2 (first_name, last_name,email,age) VALUES (:firstName, :lastName,:email,:age)");
        writer.setDataSource(dataSource);
        return writer;
    }
    
    @Bean
    public Job importUserJob1() {
        return jobBuilderFactory.get("importUserJob1")
                .incrementer(new RunIdIncrementer())
                .flow(step2())
                .end()
                .build();
    }
    
    
    @Bean
    public Step step2() {
    	System.out.println("In Step 2");
        return stepBuilderFactory.get("step2")
                .<Person, Person> chunk(100)
                .reader(readerStep2())
                .processor(processorStep2())
                .writer(writerStep2())
                .build();
    }
}