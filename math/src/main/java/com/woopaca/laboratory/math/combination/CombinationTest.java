package com.woopaca.laboratory.math.combination;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class CombinationTest {

    public static void main(String[] args) {
        int totalTickets = 100;
        int participantTickets = 5;
        int winnerCount = 3;

        double winProbability = 1.0 - (double) CombinatoricsUtils.binomialCoefficient(totalTickets - participantTickets, winnerCount)
                                      / CombinatoricsUtils.binomialCoefficient(totalTickets, winnerCount);

        System.out.printf("Win Probability: (%.2f%%)", winProbability * 100);
    }
}
