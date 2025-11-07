package com.woopaca.laboratory.datastructure;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class LinkedHashMapExample {

    public static void main(String[] args) {
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > 3;
            }
        };

        linkedHashMap.put(1, "one");
        linkedHashMap.put(2, "two");
        linkedHashMap.put(3, "three");
        linkedHashMap.get(1);
        linkedHashMap.put(4, "four");
        log.info("linkedHashMap: {}", linkedHashMap);
    }
}
