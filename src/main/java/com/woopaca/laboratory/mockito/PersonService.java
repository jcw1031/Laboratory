package com.woopaca.laboratory.mockito;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<String> getAllPersonsName() {
        return personRepository.findAllPersons()
                .stream()
                .map(person -> String.join("", person.lastName(), person.firstName()))
                .toList();
    }
}
