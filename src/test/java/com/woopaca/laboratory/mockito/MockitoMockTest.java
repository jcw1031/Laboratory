package com.woopaca.laboratory.mockito;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MockitoMockTest {

    @InjectMocks
    PersonService personService;

    @Mock
    PersonRepository personRepository;
    @Mock
    NotToMock notToMock;

    @Test
    void shouldGetAllPersonsName() {
        notToMock.printMethodName();

        Mockito.when(personRepository.findAllPersons())
                .thenReturn(List.of(new Person("찬우", "지"), new Person("안우", "지")));

        List<String> allPersonsName = personService.getAllPersonsName();
        log.info("allPersonsName = {}", allPersonsName);
    }
}
