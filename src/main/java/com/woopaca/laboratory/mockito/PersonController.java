package com.woopaca.laboratory.mockito;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/persons")
    public ResponseEntity<List<String>> allPersonsName() {
        List<String> allPersonsName = personService.getAllPersonsName();
        return ResponseEntity.ok()
                .body(allPersonsName);
    }
}
