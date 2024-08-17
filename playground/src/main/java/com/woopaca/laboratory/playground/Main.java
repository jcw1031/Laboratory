package com.woopaca.laboratory.playground;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        int T = Integer.parseInt(bf.readLine());
        int result;

        for (int i = 0; i < T; i++) {
            int n = Integer.parseInt(bf.readLine());
            Integer[] dp = new Integer[2 * n];

            String s = bf.readLine();
            String[] tokens = s.split(" ");

            for (int j = 0; j < 2 * n; j++) {
                dp[j] = Integer.parseInt(tokens[j]);
            }

            result = 0;
            while (!allZero(dp)) {
                int max = 0;
                for (int j = 0; j < 2 * n; j++) {
                    if (dp[j] > max) {
                        max = dp[j];
                    }
                }
                result += max;

                int maxIndex = Arrays.asList(dp).indexOf(max);
                dp[maxIndex] = 0;

                if ((maxIndex > 0 && maxIndex < n) || (maxIndex > n && maxIndex <= 2 * n - 1)) {
                    dp[maxIndex - 1] = 0;
                }
                if ((maxIndex < n - 1) || (maxIndex >= n && maxIndex < 2 * n - 1)) {
                    dp[maxIndex + 1] = 0;
                }
                if (maxIndex < n) {
                    dp[maxIndex + n] = 0;
                }
                if (maxIndex >= n && maxIndex <= 2 * n - 1) {
                    dp[maxIndex - n] = 0;
                }
            }
            bw.write(result + "\n");

        }
        bw.flush();
        bw.close();
    }

    private static boolean allZero(Integer[] dp) {
        for (int value : dp) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }
}