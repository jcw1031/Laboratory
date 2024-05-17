package com.woopaca.laboratory.junit5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DynamicTest {

    List<String> inputWords = List.of("apple", "banana", "melon");
    List<String> outputWords = List.of("사과", "바나나", "멜론");

    @TestFactory
    Stream<org.junit.jupiter.api.DynamicTest> translateDynamicTestsFromStream() {
        return inputWords.stream()
                .map(word ->
                        dynamicTest("Test translate " + word, () -> {
                            int index = inputWords.indexOf(word);
                            Assertions.assertEquals(outputWords.get(index), translate(word));
                        }));
    }

    String translate(String word) {
        int index = inputWords.indexOf(word);
        return outputWords.get(index);
    }
}
