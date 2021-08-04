package com.seanlubbers;

import java.util.*;

public class SavingsGoal {
    // The class to store the goal data
    // Will have a list of transactions that can be displayed via context menu or double clicking
    // Can add a new one via dialog by File -> Add new goal
    // Each goal will show the target amount, the current amount, and have options to select from preset amounts,
    // or add or remove a custom amount.
    // Target amount will be optional, if left blank it will display a label stating, "No specific target" or something.
    // Should have methods:
    // 1. To deposit funds
    // 2. To withdraw funds
    // 3. To delete a transaction
    // 4. getters for current total, target total, list of transactions (can be filtered by withdrawals or deposits, and by date)
    // 5. Modify target amount of goal
    // 6. Set name of goal
    // When a target is reached, congratulate the user and ask if they want to complete the goal.

    private List<Transaction> transactionList;
    private double target = 0.0;
    private double totalSaved = 0.0;
    private String name;

    public SavingsGoal(double target, double initialDeposit, String name, boolean loadingFromDb) {
        transactionList = new LinkedList<>();
        this.target = target;
        this.name = name;
        if(loadingFromDb) {
            totalSaved = initialDeposit;
        } else {
            deposit(initialDeposit);
        }
    }

    public SavingsGoal(double target, double initialDeposit, String name, List<Transaction> transactionList) {
        this.transactionList = transactionList;
        this.target = target;
        this.name = name;
        totalSaved = initialDeposit;
    }

    public void addGoalToDB() {
        SavingsData.getInstance().addNewSavingsGoal(this);
    }

    public boolean deposit(double amount) {
        // Amount should only be greater than zero. If not then it should be rejected and return.
        if(amount <= 0) {
            // Display message stating the input is invalid
            System.out.println("Cannot deposit " + amount + " dollars. Enter values greater than 0 only.\n");
            return false;
        }

        // If amount is greater than zero, accept the amount and add it to the total saved, creating a new
        // transaction using the current SavingsGoal name and the amount. The Transaction class will automatically
        // log the current date for us.
        totalSaved += amount;
        Transaction deposit = new Transaction(this.name, amount);
        transactionList.add(deposit);
        SavingsData.getInstance().addTransactionToGoal(this, deposit);
        return true;
    }

    public boolean withdraw(double amount) {
        // Amount should only be greater than zero. If not then it should be rejected and return.
        if(amount <= 0) {
            // Display message stating the amount is invalid
            System.out.println("Cannot withdraw " + amount + " dollars. Enter values greater than 0 only.\n");
            return false;
        }

        // If amount is greater than zero, accept the amount and remove it from the total saved, creating a new
        // transaction using the current SavingsGoal name and the amount. The Transaction class will automatically
        // log the current date for us.
        totalSaved -= amount;
        Transaction withdrawal = new Transaction(this.name, -amount);
        transactionList.add(withdrawal);
        SavingsData.getInstance().addTransactionToGoal(this, withdrawal);

        return false;
    }

    public boolean deleteTransaction(Transaction transaction) {
        // Somehow obtain which transaction the user wants to remove and obtain confirmation.
        // Since multiple transactions can be made on the same day and even for the same amount,
        // a feasible way to determine which transaction to remove would be by some kind of key.
        // Get index of selected item and remove item at index from list.
        return false;
    }

    public double updateTarget(double newTarget) {
        this.target = newTarget;
        return getTarget();
    }

    public List<Transaction> getTransactionList() {
        return this.transactionList;
    }

    public String changeName(String newName) {
        this.name = newName;
        return name;
    }








    public String getName() {
        return name;
    }

    public double getTotalSaved() {
        return totalSaved;
    }

    public double getTarget() {
        return target;
    }
}
