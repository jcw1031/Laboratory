package com.woopaca.laboratory.transaction.duplicate;

import com.woopaca.laboratory.transaction.concurrent.ParallelTest;
import com.woopaca.laboratory.transaction.duplicate.entity.CityBus;
import com.woopaca.laboratory.transaction.duplicate.repository.CityBusRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class DuplicateTest extends ParallelTest {

    @Autowired
    CityBusRepository cityBusRepository;

    @Autowired
    CityBusService cityBusService;

    @BeforeEach
    void setUp() {
        LocalTime currentTime = LocalTime.of(15, 0, 0);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            CityBus cityBus = new CityBus(String.valueOf(i));

            Set<LocalTime> arrivalTimes = new HashSet<>();
            for (int j = 0; j < 5; j++) {
                int randomNumber = random.nextInt(30);
                arrivalTimes.add(currentTime.plusMinutes(randomNumber));
            }
            cityBus.updateArrivalTimes(arrivalTimes);

            cityBusRepository.save(cityBus);
        }
    }

    @Test
    void duplicateEntryTest() throws InterruptedException {
        executionParallel(integer -> cityBusService.updateCityBusArrivalTimes(), 2);
    }
}
