package com.woopaca.laboratory.math.hypergeometric;

import org.apache.commons.math3.distribution.HypergeometricDistribution;

public class HypergeometricTest {

    public static void main(String[] args) {
        int totalTickets = 100;
        int participantTickets = 30;
        int winnerCount = 3;

        HypergeometricDistribution distribution =
                new HypergeometricDistribution(totalTickets, participantTickets, winnerCount);

        double winProbability = 1.0 - distribution.probability(0);

        System.out.printf("Win Probability: (%.2f%%)", winProbability * 100);
    }
}
