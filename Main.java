package banking;

import java.util.Random;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();
    static String number;
    static String pin;

    public static void main(String[] args) {
        Database.createTable(args[1]);
        mainMenu();
    }

    static void mainMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
        initializeMainAction();
    }

    static void initializeMainAction() {
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                createAccount();
                break;

            case 2:
                login();
                break;

            case 0:
                exit();
                break;

            default:
                System.out.println("wrong input");
                mainMenu();
                break;
        }
    }

    static void createAccount() {
        getData();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(number);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        mainMenu();
    }

    static void login() {
        System.out.println("Enter your card number:");
        long n = scanner.nextLong();
        System.out.println("Enter your PIN:");
        int p = scanner.nextInt();
        boolean loggedIn = false;
        if (!verifyChecksum(n)) {
            System.out.println("Wrong card number!");
        } else {
            loggedIn = Database.isLoggedIn(String.valueOf(n), String.valueOf(p));
        }
        if (loggedIn) {
            System.out.println("You have successfully logged in!");
            number = String.valueOf(n);
            pin = String.valueOf(p);
            loggedInMenu();
        } else {
            System.out.println("Wrong card number or PIN!");
            mainMenu();
        }
    }

    static void loggedInMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
        initializeLogAction();
    }

    static void initializeLogAction() {
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                balance();
                break;

            case 2:
                addIncome();
                break;

            case 3:
                doTransfer();
                break;

            case 4:
                closeAccount();
                break;

            case 5:
                logout();
                break;

            case 0:
                exit();
                break;

            default:
                System.out.println("Wrong input!");
                loggedInMenu();
                break;
        }
    }

    static void exit() {
        System.out.println("Bye!");
    }

    static void balance() {
        System.out.println(Database.getBalance(number, pin));
        loggedInMenu();
    }

    static void addIncome() {
        System.out.println("Enter income:");
        int toAdd = scanner.nextInt();
        boolean done = Database.depositMoney(number, toAdd);
        if (done) {
            System.out.println("Income was added!");
        } else {
            System.out.println("Error while adding income");
        }
        loggedInMenu();
    }

    static void doTransfer() {
        System.out.println("Enter card number:");
        long toCard = scanner.nextLong();
        if (toCard == Long.parseLong(number)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!verifyChecksum(toCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!Database.doesCardExist(String.valueOf(toCard))) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int transferAmount = scanner.nextInt();
            if (Database.getBalance(number, pin) < transferAmount) {
                System.out.println("Not enough money!");
            } else {
                boolean done = Database.transferMoney(number, String.valueOf(toCard), transferAmount);
                if (done) {
                    System.out.println("Success!");
                } else {
                    System.out.println("Error while transferring money");
                }
            }
        }
        loggedInMenu();
    }

    static void closeAccount() {
        Database.closeAcc(number);
        System.out.println("The account has been closed!");
        number = null;
        pin = null;
        mainMenu();
    }

    static void logout() {
        System.out.println("You have successfully logged out!");
        mainMenu();
        number = null;
        pin = null;
    }

    static void getData() {
        boolean isUnique = false;
        String temp1 = null;
        String temp2 = null;
        while (!isUnique) {
            temp1 = createNumber();
            temp2 = createPin();
            isUnique = !Database.doesCardExist(temp1);
        }
        number = temp1;
        pin = temp2;
        Database.addCreditCard(number, pin);
    }

    static String createNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(400000);

        // (upper - lower + 1) + lower
        // (9 - 0 + 1) + 0
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        sb.append(getChecksum(sb));
        return sb.toString();
    }

    static String createPin() {
        StringBuilder sb = new StringBuilder();
        // (upper - lower + 1) + lower
        // (9 - 1 + 1) + 1
        sb.append(random.nextInt(9) + 1);

        // (upper - lower + 1) + lower
        // (9 - 0 + 1) + 0
        for (int i = 0; i < 3; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    static int getChecksum(StringBuilder sb) {
        String[] arr = sb.toString().split("");
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            int temp = Integer.parseInt(arr[i]);
            if ((i + 1) % 2 != 0) {
                temp *= 2;
            }
            if (temp > 9) {
                temp -= 9;
            }
            sum += temp;
        }
        return (10 - (sum % 10)) % 10;
    }

    static boolean verifyChecksum(long n) {
        int checksum = (int) (n % 10);
        n /= 10;
        String[] arr = String.valueOf(n).split("");
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            int temp = Integer.parseInt(arr[i]);
            if ((i + 1) % 2 != 0) {
                temp *= 2;
            }
            if (temp > 9) {
                temp -= 9;
            }
            sum += temp;
        }
        return (sum + checksum) % 10 == 0;
    }
}