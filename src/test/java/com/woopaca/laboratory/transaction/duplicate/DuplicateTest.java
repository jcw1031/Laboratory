package com.woopaca.laboratory.transaction.duplicate;

import com.woopaca.laboratory.transaction.duplicate.domain.CityBus;
import com.woopaca.laboratory.transaction.duplicate.repository.CityBusRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Transactional
@Rollback(value = false)
@SpringBootTest
public class DuplicateTest {

    @Autowired
    private CityBusRepository cityBusRepository;

    @Test
    void duplicateEntryTest() {
        List<CityBus> buses = cityBusRepository.findAll();
        List<CityBus> result = buses.stream()
                .peek(cityBus -> {
                    log.info("cityBus = {}, time = {}", cityBus.getBusNumber(), cityBus.getArrivalTimes());
                })
                .toList();

        LocalTime time1 = LocalTime.of(17, 31, 25);
        LocalTime time2 = LocalTime.of(17, 42, 20);
        LocalTime time3 = LocalTime.of(17, 52, 20);

        result.stream()
                .forEach(cityBus -> {
                    cityBus.updateArrivalTimes(List.of(time1, time2, time3));
                    cityBusRepository.save(cityBus);
                });
    }
}
