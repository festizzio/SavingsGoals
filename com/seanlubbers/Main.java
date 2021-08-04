package com.seanlubbers;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    public static void main(String[] args) {
        if(SavingsData.getInstance().open()) {
            System.out.println("Loading database connection...");
            System.out.println("Successfully opened database connection");
        } else {
            System.out.println("Unable to open database connection");
        }


//        SavingsGoal goal = new SavingsGoal(1000, 100, "Piano");
//        goal.addGoalToDB();
//	    if(goal.deposit(500)) {
//            System.out.println("Successfully deposited $500!");
//            System.out.println("New total: $" + goal.getTotalSaved());
//        }
//
//        if(goal.deposit(-100)) {
//            System.out.println("Successfully deposited $100!");
//            System.out.println("New total: $" + goal.getTotalSaved());
//        }
        System.out.println("1. Add new goal");
        System.out.println("2. Deposit money to goal");
        System.out.println("3. Withdraw money from goal");
        System.out.println("4. View goals");
        System.out.println("5. Delete goal");
        System.out.println("6. View transactions");
        System.out.println("7. Quit program\n");
        System.out.println("Enter your selection: ");

        int choice;
        boolean quit = false;
        if(scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine();
            while(!quit) {
                switch(choice) {
                    case 1:
                        addNewGoal();
                        break;
                    case 2:
                        deposit();
                        break;
                    case 3:
                        withdraw();
                        break;
                    case 4:
                        viewGoals();
                        System.out.println("\nPress enter to continue...");
                        scanner.nextLine();
                        break;
                    case 5:
                        deleteGoal();
                        break;
                    case 6:
                        viewTransactions();
                        System.out.println("\nPress enter to continue...");
                        scanner.nextLine();
                        break;
                    case 7:
                        quit = true;
                        System.out.println("Quitting program...");
                        SavingsData.getInstance().close();
                        continue;
                        //quitProgram();
                    default:
                        //display message rejecting input, must be from 1 to 5
                        break;
                }

                System.out.println("Complete!");

                System.out.println("1. Add new goal");
                System.out.println("2. Deposit money to goal");
                System.out.println("3. Withdraw money from goal");
                System.out.println("4. View goals");
                System.out.println("5. Delete goal");
                System.out.println("6. View transactions");
                System.out.println("7. Quit program\n");
                System.out.println("Enter your selection: ");

                while(!scanner.hasNextInt()) {
                    System.out.println("Invalid selection, try again.");
                }
                choice = scanner.nextInt();
                scanner.nextLine();
            }
        }
    }

    public static void addNewGoal() {
        System.out.print("Enter the new goal's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter the target total for the goal " + name + ": ");
        while(!scanner.hasNextDouble()) {
            System.out.println("Invalid entry, must be a number.");
            System.out.print("Enter the target total for the goal " + name + ": ");
        }
        double targetAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter initial deposit for " + name + ": ");
        while(!scanner.hasNextDouble()) {
            System.out.println("Invalid entry, must be a number.");
            System.out.print("Enter the initial deposit for the goal " + name + ": ");
        }
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();
        SavingsGoal newGoal = new SavingsGoal(targetAmount, initialDeposit, name, false);
        newGoal.addGoalToDB();
    }

    public static void deposit() {
        System.out.println("Select goal to deposit to:");
        Map<String, SavingsGoal> goals = SavingsData.getInstance().getGoals();
        int i = 0;
        Map<Integer, String> goalsReference = new HashMap<>();
        for(SavingsGoal goal : goals.values()) {
            System.out.println((i+1) + ". " + goal.getName());
            goalsReference.put(i, goal.getName());
            i++;
        }
        System.out.print("Enter your selection: ");
        while(!scanner.hasNextInt()) {
            System.out.print("Invalid entry, please enter the number of your selection: ");
        }
        int selection = scanner.nextInt();
        scanner.nextLine();
        String newGoalName = goalsReference.get(selection - 1);
        SavingsGoal goalToDeposit = goals.get(newGoalName);
        System.out.print("Enter amount to deposit to " + newGoalName + ": ");
        while(!scanner.hasNextDouble()) {
            System.out.println("Invalid entry, must be a number.");
            System.out.print("Enter amount to deposit to " + newGoalName + ": ");
        }
        double transactionAmount = scanner.nextDouble();
        scanner.nextLine();
        goalToDeposit.deposit(transactionAmount);
    }

    public static void withdraw() {
        System.out.println("Select goal to withdraw from:");
        Map<String, SavingsGoal> goals = SavingsData.getInstance().getGoals();
        int i = 0;
        Map<Integer, String> goalsReference = new HashMap<>();
        for(SavingsGoal goal : goals.values()) {
            System.out.println((i+1) + ". " + goal.getName());
            goalsReference.put(i, goal.getName());
            i++;
        }
        System.out.print("Enter your selection: ");
        while(!scanner.hasNextInt()) {
            System.out.print("Invalid entry, please enter the number of your selection: ");
        }
        int selection = scanner.nextInt();
        scanner.nextLine();
        String newGoalName = goalsReference.get(selection - 1);
        SavingsGoal goalToWithdraw = goals.get(newGoalName);
        System.out.print("Enter amount to withdraw from " + newGoalName + ": ");
        while(!scanner.hasNextDouble()) {
            System.out.println("Invalid entry, must be a number.");
            System.out.print("Enter amount to withdraw from " + newGoalName + ": ");
        }
        double transactionAmount = scanner.nextDouble();
        scanner.nextLine();
        goalToWithdraw.withdraw(transactionAmount);
    }

    public static void viewGoals() {
        System.out.println("Goal Name\tTarget Amount\tTotal Saved");
        System.out.println("===============================================");
        Map<String, SavingsGoal> goals = SavingsData.getInstance().getGoals();
        for(SavingsGoal goal : goals.values()) {
            System.out.println(goal.getName() + "\t\t" + goal.getTarget() + "\t\t\t" + goal.getTotalSaved());
        }

    }

    public static void deleteGoal() {
        viewGoals();
        System.out.println("Enter name of goal to delete: ");
        String goalToDelete = scanner.nextLine();
        System.out.println("Are you sure you want to delete the goal " + goalToDelete + "? (Y/N)");
        String choice = scanner.nextLine();
        if(choice.matches("[Yy]")) {
            if(SavingsData.getInstance().deleteGoalData(goalToDelete)) {
                System.out.println("Successfully deleted " + goalToDelete + " goal!");
            } else {
                System.out.println("Unable to delete " + goalToDelete + ". Encountered an error or goal not found.");
            }
        } else {
            System.out.println("Cancelling delete request...");
        }
    }

    public static void viewTransactions() {
        viewGoals();
        System.out.println("Enter name of goal to view transactions: ");
        String goalToView = scanner.nextLine();
        if(SavingsData.getInstance().getGoals().containsKey(goalToView)) {
            List<Transaction> transactionList = SavingsData.getInstance().getGoals().get(goalToView).getTransactionList();
            System.out.println("Goal Name\tTransaction Date\tTransaction Amount");
            System.out.println("=======================================================");
            for (Transaction transaction : transactionList) {
                System.out.println(goalToView + "\t\t" + transaction.getTransactionDate() + "\t\t" +
                        numberFormat.format(transaction.getAmount()));
            }
        } else {
            System.out.println("Goal " + goalToView + " not found in goal list. No transactions found.");
        }
    }
}
