package com.woopaca.laboratory.thread.concurrency;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Account {

    private double balance;

    public boolean safeWithdraw(final int amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
        }
        return false;
    }
}
