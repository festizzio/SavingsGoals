package com.seanlubbers;

import java.time.LocalDate;

public class Transaction {
    // Each addition or subtraction in the SavingsGoal will be of type Transaction
    // Transaction will have a double dollarAmount, boolean withdrawal or deposit, date
    // They will be displayed via dialog by clicking on a "Show transaction history" button

    private final double amount;
    private final LocalDate transactionDate;
    private final String goalName;

    public Transaction(String goalName, double amount) {
        this.goalName = goalName;
        this.amount = amount;
        // When a new transaction is created, it should always set the date of the transaction to the current date.
        // The program will not allow older transactions. This isn't super important anyway, since it's for savings.
        // Even if the user forgets to add all of their transactions, the fact that they all show the same date won't be an issue.
        this.transactionDate = LocalDate.now();
    }

    public Transaction(String goalName, double amount, LocalDate transactionDate) {
        this.goalName = goalName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

}
