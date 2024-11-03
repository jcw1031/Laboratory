package com.woopaca.laboratory.database.batchfetching;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest(showSql = false)
class HibernateBatchSizeTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CatRepository catRepository;

    @BeforeEach
    void setUp() {
        personRepository.save(new Person("Alice"));
        personRepository.save(new Person("Bob"));
        personRepository.save(new Person("Charlie"));
        personRepository.save(new Person("David"));
        personRepository.save(new Person("Eve"));
        personRepository.save(new Person("Frank"));
        personRepository.save(new Person("Chan"));

        catRepository.save(new Cat("A", personRepository.findById(1L).get()));
        catRepository.save(new Cat("B", personRepository.findById(2L).get()));
        catRepository.save(new Cat("C", personRepository.findById(2L).get()));
        catRepository.save(new Cat("D", personRepository.findById(3L).get()));
        catRepository.save(new Cat("E", personRepository.findById(4L).get()));
        catRepository.save(new Cat("F", personRepository.findById(4L).get()));
        catRepository.save(new Cat("G", personRepository.findById(4L).get()));
        catRepository.save(new Cat("H", personRepository.findById(5L).get()));
        catRepository.save(new Cat("I", personRepository.findById(5L).get()));
        catRepository.save(new Cat("J", personRepository.findById(6L).get()));
        catRepository.save(new Cat("K", personRepository.findById(7L).get()));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testBatchSize() {
        personRepository.findAll()
                .forEach(person -> {
                    System.out.println(person.getName());
                    System.out.println(person.getCats().size());
                });
    }
}