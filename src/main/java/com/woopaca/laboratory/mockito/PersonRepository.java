package com.woopaca.laboratory.mockito;

import com.woopaca.laboratory.batchprocessing.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository  {

    public List<Person> findAllPersons() {
        return List.of(new Person("찬우", "지"), new Person("안우", "지"));
    }
}
