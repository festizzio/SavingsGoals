package com.seanlubbers;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class SavingsData {
    // Data source class, deals with loading and saving data to the SQLite database.
    // Will have methods for adding new transactions to the db, addition and subtraction.
    // Store List<SavingsGoal> and will be able to check if a goal of the same name already exists.
    // Map<String, List<SavingsGoal>> using the name of the goal as the string, if Map containsKey(newName) then reject
    // Should have methods to:
    // 1. Add new savings goal
    // 2. Move money between specific goals
    // 3. Change name of a goal
    // 4. Delete a goal
    private static final String DB_NAME = "Goals.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:C:/Users/festi/IdeaProjects/Savings Goals Terminal/src/" + DB_NAME;
//    private static final String CREATE_GOALS_DB = "CREATE DATABASE IF NOT EXISTS Goals";

    private static final String GOAL_TABLE_NAME = "goals";
    private static final String COLUMN_GOAL_NAME = "goal_name";
    private static final int INDEX_GOAL_NAME = 1;
    private static final String COLUMN_TOTAL_SAVED = "total_saved";
    private static final int INDEX_TOTAL_SAVED = 3;
    private static final String COLUMN_TARGET_TOTAL = "target_total";
    private static final int INDEX_TARGET_TOTAL = 2;
//    private final String CREATE_GOAL_TABLE = "CREATE TABLE IF NOT EXISTS " + GOAL_TABLE_NAME + "(\n" +
//            COLUMN_GOAL_NAME + " TEXT NOT NULL,\n" +
//            COLUMN_TARGET_TOTAL + " REAL NOT NULL,\n" +
//            COLUMN_TOTAL_SAVED + " REAL NOT NULL)\n";
    private static final String QUERY_GOALS = "SELECT * FROM " + GOAL_TABLE_NAME;

    private static final String TRANSACTION_TABLE_NAME = "transactions";
    private static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    private static final String COLUMN_TRANSACTION_AMOUNT = "transaction_amount";
//    private final String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE_NAME + "(\n" +
//            COLUMN_GOAL_NAME + " TEXT NOT NULL,\n" +
//            COLUMN_TRANSACTION_DATE + " TEXT NOT NULL,\n" +
//            COLUMN_TRANSACTION_AMOUNT + " REAL NOT NULL)\n";

    private static final String INSERT_NEW_GOAL = "INSERT INTO " + GOAL_TABLE_NAME + " (" + COLUMN_GOAL_NAME + ", " +
            COLUMN_TARGET_TOTAL + ", " + COLUMN_TOTAL_SAVED + ") VALUES (?, ?, ?)";
    private static final String INSERT_NEW_TRANSACTION = "INSERT INTO " + TRANSACTION_TABLE_NAME + " (" + COLUMN_GOAL_NAME + ", " +
            COLUMN_TRANSACTION_DATE + ", " + COLUMN_TRANSACTION_AMOUNT + ") VALUES (?, ?, ?)";
    private static final int INDEX_TRANSACTION_DATE = 2;
    private static final int INDEX_TRANSACTION_AMOUNT = 3;
    private static final String QUERY_TRANSACTIONS = "SELECT * FROM " + TRANSACTION_TABLE_NAME + " WHERE " + COLUMN_GOAL_NAME +
            " = ?";

    private static final String UPDATE_GOAL_TOTAL = "UPDATE " + GOAL_TABLE_NAME + " SET " + COLUMN_TOTAL_SAVED + " = ? WHERE " +
            COLUMN_GOAL_NAME + " = ?";

    private static final String DELETE_GOAL = "DELETE FROM " + GOAL_TABLE_NAME + " WHERE " + COLUMN_GOAL_NAME + " = ?";
    private static final String DELETE_GOAL_TRANSACTIONS = "DELETE FROM " + TRANSACTION_TABLE_NAME + " WHERE " + COLUMN_GOAL_NAME +
            " = ?";

    // Prepared statements to create new goals, update existing goals, and delete goals.
    // May add one to complete goals without deleting them as well, for example when the target is reached.
    // These are prepared using the above INSERT statements.
    PreparedStatement insertNewGoal;
    PreparedStatement insertNewTransaction;
    PreparedStatement transactionStatement;
    PreparedStatement updateGoalTotal;
    PreparedStatement deleteGoal;
    PreparedStatement deleteTransactions;

    private static SavingsData savingsData = new SavingsData();
    private Map<String, SavingsGoal> goals = new HashMap<>();

    private Connection conn;

    // This is going to be a data singleton class.
    public static SavingsData getInstance() {
        if(savingsData == null) {
            savingsData = new SavingsData();
        }
        return savingsData;
    }

    // Need to open the connection to the database, and since the constructor is private, this is the best/only way to do it.
    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            try {
                conn.setAutoCommit(false);
            } catch(SQLException e) {
                System.out.println("Error setting auto commit to false in addNewSavingsGoal");
                e.printStackTrace();
            }
            loadSavingsGoals();
            return true;
        } catch(SQLException e) {
            System.out.println("Error opening database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (insertNewGoal != null) {
                insertNewGoal.close();
            }
            if(insertNewTransaction != null) {
                insertNewTransaction.close();
            }
            if(transactionStatement != null) {
                transactionStatement.close();
            }
        } catch(SQLException e) {
            System.out.println("Error closing prepared statements and connection");
        }

    }

    private SavingsData() {
    }

    // Since this is only needed to update the SQLite database, just pass the SavingsGoal object
    // and use the getters from the goal object to write the data.
    public void addNewSavingsGoal(SavingsGoal goal) {
        try {
            insertNewGoal = conn.prepareStatement(INSERT_NEW_GOAL);
            insertNewGoal.setString(1, goal.getName());
            insertNewGoal.setDouble(2, goal.getTarget());
            insertNewGoal.setDouble(3, goal.getTotalSaved());
            insertNewGoal.execute();
            conn.commit();
            goals.put(goal.getName(), goal);
        } catch(SQLException e) {
            System.out.println("Error creating prepared statement in SavingsData: " + e.getMessage());
        }
    }

    public void addTransactionToGoal(SavingsGoal goal, Transaction transaction) {
        try {
            insertNewTransaction = conn.prepareStatement(INSERT_NEW_TRANSACTION);
            // 1. goal name
            // 2. transaction date
            // 3. transaction amount
            insertNewTransaction.setString(INDEX_GOAL_NAME, goal.getName());
            insertNewTransaction.setDouble(INDEX_TRANSACTION_AMOUNT, transaction.getAmount());
            insertNewTransaction.setString(INDEX_TRANSACTION_DATE, transaction.getTransactionDate().toString());

            updateGoalTotal = conn.prepareStatement(UPDATE_GOAL_TOTAL);
            updateGoalTotal.setDouble(1, goal.getTotalSaved());
            updateGoalTotal.setString(2, goal.getName());

            insertNewTransaction.execute();
            updateGoalTotal.execute();
            conn.commit();
        } catch(SQLException e) {
            System.out.println("Error inserting transaction for " + transaction.getAmount());
            e.printStackTrace();
        }
    }

    public void loadSavingsGoals() {
        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(QUERY_GOALS)) {
            while(results.next()) {
                String goalName = results.getString(INDEX_GOAL_NAME);
                double target = results.getDouble(INDEX_TARGET_TOTAL);
                double totalSaved = 0;
                List<Transaction> goalTransactions = new LinkedList<>();
                try {
                    transactionStatement = conn.prepareStatement(QUERY_TRANSACTIONS);
                    transactionStatement.setString(INDEX_GOAL_NAME, goalName);
                    try (ResultSet transactionResults = transactionStatement.executeQuery()) {
                        while(transactionResults.next()) {
                            double transactionAmount = transactionResults.getDouble(INDEX_TRANSACTION_AMOUNT);
                            LocalDate transactionDate = LocalDate.parse(transactionResults.getString(INDEX_TRANSACTION_DATE));
                            goalTransactions.add(new Transaction(goalName, transactionAmount, transactionDate));
                            totalSaved += transactionAmount;
                        }
                    } catch(SQLException f) {
                        System.out.println("Error preparing statement for finding goal transactions");
                        f.printStackTrace();
                    }
                } catch(SQLException g) {
                    System.out.println("Error creating prepared statement for finding all transactions");
                    g.printStackTrace();
                }
                goals.put(goalName, new SavingsGoal(target, totalSaved, goalName, goalTransactions));

            }
        } catch(SQLException e) {
            System.out.println("Error reading goals from goals table: " + e.getMessage());
        }
    }

    public boolean deleteGoalData(String goalToDelete) {
        if(goals.containsKey(goalToDelete)) {
            try(PreparedStatement deleteGoal = conn.prepareStatement(DELETE_GOAL);
                PreparedStatement deleteTransactions = conn.prepareStatement(DELETE_GOAL_TRANSACTIONS)) {
                deleteGoal.setString(INDEX_GOAL_NAME, goalToDelete);
                deleteTransactions.setString(INDEX_GOAL_NAME, goalToDelete);
                deleteGoal.execute();
                deleteTransactions.execute();
            } catch(SQLException e) {
                System.out.println("Error preparing statement to delete goal or transactions: " +
                        e.getMessage());
                e.printStackTrace();
            }
            try {
                conn.commit();
            } catch(SQLException e) {
                System.out.println("Error committing changes to delete goal " + goalToDelete + " and associated transactions.");
                e.printStackTrace();
                return false;
            }
            goals.remove(goalToDelete);
            return true;
        } else {
            System.out.println("Error deleting goal " + goalToDelete + ", goal not found.");
            return false;
        }
    }

    public Map<String, SavingsGoal> getGoals() {
        return goals;
    }

//    public void createGoalsTable() {
//        try (Statement statement = conn.createStatement()) {
//            statement.execute(CREATE_GOAL_TABLE);
//            statement.execute(CREATE_TRANSACTION_TABLE);
//        } catch(Exception e) {
//            System.out.println("Error creating statement in createGoalsTable: " + e.getMessage());
//        } finally {
//            close();
//        }
//    }
}
