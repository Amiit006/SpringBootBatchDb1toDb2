package com.infotech.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.infotech.batch.model.Person;

public class PersonItemProcessor2 implements ItemProcessor<Person, Person> {

    //private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor2.class);

    @Override
    public Person process(final Person person) throws Exception {
        final String firstName = person.getFirstName().toLowerCase();
        final String lastName = person.getLastName().toLowerCase();

        final Person transformedPerson = new Person(firstName, lastName,person.getEmail(),person.getAge());

        //log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}