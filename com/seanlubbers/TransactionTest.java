package com.seanlubbers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @org.junit.jupiter.api.Test
    void testDateGeneration() {
        Transaction transaction = new Transaction("Piano", 500);
        assertEquals(LocalDate.now(), transaction.getTransactionDate());
    }

    @org.junit.jupiter.api.Test
    void testAmountInitialization() {
        Transaction transaction = new Transaction("Friends",100);
        assertEquals(100, transaction.getAmount());
        transaction = new Transaction("Parents",500);
        assertEquals(500, transaction.getAmount());
    }

    @org.junit.jupiter.api.Test
    void testDateFunction() {
        Transaction transaction = new Transaction("Parents", 500);
        assertEquals(5, transaction.getTransactionDate().getMonthValue());
        assertEquals(5, transaction.getTransactionDate().getDayOfMonth());
        assertEquals(2021, transaction.getTransactionDate().getYear());
        assertEquals("2021-05-05", transaction.getTransactionDate().toString());
    }

    @org.junit.jupiter.api.Test
    void testTransactionTotal() {

    }
}