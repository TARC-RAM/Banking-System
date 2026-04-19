import java.util.Scanner;
import java.util.ArrayList;

public class AdminMenu {
    private final ReportManager rm = new ReportManager();
    private final FileManager fm = new FileManager();

    public void displayMenu() {
        displayMenu(new Scanner(System.in));
    }

    public void displayMenu(Scanner sc) {
        while (true) {
            ArrayList<BankAccountRecord> bankAccounts = fm.loadBankAccounts();
            ArrayList<AccountDetails> accountProfiles = fm.loadAccounts();
            ArrayList<Transaction> transactions = fm.loadTransactions();
            ArrayList<UserCredentials> users = fm.loaduser();

            TerminalUI.clearScreen();
            TerminalUI.printCentered("╔════════════════════════════════════╗");
            TerminalUI.printCentered("║            ADMIN PANEL             ║");
            TerminalUI.printCentered("╚════════════════════════════════════╝");
            TerminalUI.printCentered("1. Freeze / Unfreeze Account");
            TerminalUI.printCentered("2. Set Savings Interest Rate");
            TerminalUI.printCentered("3. Generate Monthly Statement");
            TerminalUI.printCentered("4. View Full Account Data");
            TerminalUI.printCentered("5. Delete Account");
            TerminalUI.printCentered("6. View Full Transaction History");
            TerminalUI.printCentered("0. Exit");

            int choice = readMenuChoice(sc, "Choice: ");
            if (choice == 0) {
                break;
            }
            if (choice < 1 || choice > 6) {
                TerminalUI.printCentered(">>> [Invalid Input] Please enter 1, 2, 3, 4, 5, 6, or 0.");
                TerminalUI.pauseForEnter(sc);
                continue;
            }

            if (bankAccounts.isEmpty()) {
                TerminalUI.printCentered(">>> [Error] No bank accounts found.");
                TerminalUI.pauseForEnter(sc);
                continue;
            }

            System.out.print(TerminalUI.menuPrompt("Enter Account Number (e.g. ACC1000): "));
            String accNum = sc.nextLine().trim();

            BankAccountRecord target = findAccountByNumber(accNum, bankAccounts);

            if (target == null) {
                TerminalUI.printCentered(">>> [Error] Account not found!");
                TerminalUI.pauseForEnter(sc);
                continue;
            }

            handleChoice(choice, target, accountProfiles, transactions, bankAccounts, users, sc);
            TerminalUI.pauseForEnter(sc);
        }
    }

    private BankAccountRecord findAccountByNumber(String accountNumber, ArrayList<BankAccountRecord> bankAccounts) {
        for (BankAccountRecord account : bankAccounts) {
            if (accountNumber.equals(account.getAccountNumber())) {
                return account;
            }
        }
        return null;
    }

    private AccountDetails findProfile(BankAccountRecord bankAccount, ArrayList<AccountDetails> accountProfiles) {
        for (AccountDetails profile : accountProfiles) {
            if (bankAccount.getUserUUID() != null && bankAccount.getUserUUID().equals(profile.getUUID())) {
                return profile;
            }
        }
        for (AccountDetails profile : accountProfiles) {
            if (bankAccount.getLinkedProfileAccountNumber() != null
                    && bankAccount.getLinkedProfileAccountNumber().equals(profile.getAccountNumber())) {
                return profile;
            }
        }
        return null;
    }

    private UserCredentials findUser(BankAccountRecord bankAccount, AccountDetails profile, ArrayList<UserCredentials> users) {
        for (UserCredentials user : users) {
            if (bankAccount.getUserUUID() != null && bankAccount.getUserUUID().equals(user.getUUID())) {
                return user;
            }
        }
        if (profile != null) {
            for (UserCredentials user : users) {
                if (profile.getUsername() != null && profile.getUsername().equals(user.getUsername())) {
                    return user;
                }
            }
        }
        for (UserCredentials user : users) {
            if (bankAccount.getUsername() != null && bankAccount.getUsername().equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    private void handleChoice(int choice, BankAccountRecord target,
                              ArrayList<AccountDetails> accountProfiles,
                              ArrayList<Transaction> trans,
                              ArrayList<BankAccountRecord> bankAccounts,
                              ArrayList<UserCredentials> users,
                              Scanner sc) {
        if (choice == 1) {
            target.setFrozen(!target.isFrozen());
            fm.saveBankAccounts(bankAccounts);

            String statusLabel = target.isFrozen() ? "[ LOCKED ]" : "[ ACTIVE ]";
            String description = target.isFrozen() ? "Account has been restricted." : "Account is now functional.";
            
            TerminalUI.printCentered("╔════════════════════════════════════════════╗");
            TerminalUI.printCentered("║       ACCOUNT SECURITY UPDATE              ║");
            TerminalUI.printCentered("╠════════════════════════════════════════════╣");
            TerminalUI.printCentered("ACTION: Account Security Update");
            TerminalUI.printCentered(String.format("STATUS: %-15s %s", statusLabel, description));
            TerminalUI.printCentered("╚════════════════════════════════════════════╝");
        }
        else if (choice == 2) {
            if ("Savings".equalsIgnoreCase(target.getAccountType())) {
                double rate = readPositiveRate(sc);
                target.setInterestRate(rate);
                fm.saveBankAccounts(bankAccounts);
                TerminalUI.printCentered("Interest rate updated successfully.");
            } else {
                TerminalUI.printCentered("This is a Current Account, no interest rate to set.");
            }
        }
        else if (choice == 3) {
            AccountDetails profile = findProfile(target, accountProfiles);
            rm.printMonthlyStatement(target, profile, trans);
        }
        else if (choice == 4) {
            AccountDetails profile = findProfile(target, accountProfiles);
            UserCredentials user = findUser(target, profile, users);
            printFullAccountData(target, profile, user, trans);
        }
        else if (choice == 5) {
            AccountDetails profile = findProfile(target, accountProfiles);
            UserCredentials user = findUser(target, profile, users);
            deleteAccount(target, user, accountProfiles, trans, bankAccounts, users, sc);
        }
        else if (choice == 6) {
            printTransactionHistory(target, trans);
        }
    }

    private void deleteAccount(BankAccountRecord target,
                               UserCredentials user,
                               ArrayList<AccountDetails> accountProfiles,
                               ArrayList<Transaction> transactions,
                               ArrayList<BankAccountRecord> bankAccounts,
                               ArrayList<UserCredentials> users,
                               Scanner sc) {
        System.out.print(TerminalUI.menuPrompt("Type DELETE to confirm deletion: "));
        String confirmation = sc.nextLine().trim();
        if (!"DELETE".equals(confirmation)) {
            TerminalUI.printCentered("Deletion cancelled.");
            return;
        }

        bankAccounts.remove(target);
        fm.saveBankAccounts(bankAccounts);

        int removedProfiles = removeLinkedProfiles(target, accountProfiles);
        if (removedProfiles > 0) {
            fm.saveAccounts(accountProfiles);
        }

        int removedTransactions = removeLinkedTransactions(target, transactions);
        if (removedTransactions > 0) {
            fm.saveTransactions(transactions);
        }

        boolean removedUser = false;
        if (user != null && !hasOtherBankAccounts(target, bankAccounts)) {
            removedUser = removeLinkedUser(user, users);
            if (removedUser) {
                fm.saveUsers(users);
            }
        }

        TerminalUI.printCentered(">>> [Success] Account deleted.");
        TerminalUI.printCentered("    Profiles removed     : " + removedProfiles);
        TerminalUI.printCentered("    Transactions removed : " + removedTransactions);
        TerminalUI.printCentered("    User credential removed: " + (removedUser ? "Yes" : "No"));
    }

    private int removeLinkedProfiles(BankAccountRecord target, ArrayList<AccountDetails> accountProfiles) {
        String linkedProfileAccountNumber = target.getLinkedProfileAccountNumber();
        int removed = 0;

        for (int i = accountProfiles.size() - 1; i >= 0; i--) {
            AccountDetails profile = accountProfiles.get(i);
            boolean matchByAccountNumber = linkedProfileAccountNumber != null
                    && linkedProfileAccountNumber.equals(profile.getAccountNumber());
            if (matchByAccountNumber) {
                accountProfiles.remove(i);
                removed++;
            }
        }

        return removed;
    }

    private int removeLinkedTransactions(BankAccountRecord target, ArrayList<Transaction> transactions) {
        String targetUUID = target.getUserUUID();
        String targetAccountNumber = target.getAccountNumber();
        int removed = 0;

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            boolean matchByUUID = targetUUID != null && targetUUID.equals(transaction.getUserUUID());
            boolean matchByAccount = targetAccountNumber != null && targetAccountNumber.equals(transaction.getAccountNumber());
            boolean shouldRemove = targetUUID != null ? (matchByUUID && matchByAccount) : matchByAccount;
            if (shouldRemove) {
                transactions.remove(i);
                removed++;
            }
        }

        return removed;
    }

    private boolean hasOtherBankAccounts(BankAccountRecord target, ArrayList<BankAccountRecord> bankAccounts) {
        for (BankAccountRecord account : bankAccounts) {
            if (target.getUserUUID() != null && target.getUserUUID().equals(account.getUserUUID())) {
                return true;
            }
            if (target.getUserUUID() == null
                    && target.getUsername() != null
                    && target.getUsername().equals(account.getUsername())) {
                return true;
            }
        }
        return false;
    }

    private boolean removeLinkedUser(UserCredentials user, ArrayList<UserCredentials> users) {
        for (int i = users.size() - 1; i >= 0; i--) {
            UserCredentials candidate = users.get(i);
            boolean matchByUUID = user.getUUID() != null && user.getUUID().equals(candidate.getUUID());
            boolean matchByUsername = user.getUUID() == null
                    && user.getUsername() != null
                    && user.getUsername().equals(candidate.getUsername());
            if (matchByUUID || matchByUsername) {
                users.remove(i);
                return true;
            }
        }
        return false;
    }

    private int readMenuChoice(Scanner sc, String prompt) {
        while (true) {
            System.out.print(TerminalUI.menuPrompt(prompt));
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                TerminalUI.printCentered(">>> [Invalid Input] Please enter a number.");
            }
        }
    }

    private double readPositiveRate(Scanner sc) {
        while (true) {
            System.out.print(TerminalUI.menuPrompt("Enter new Rate: "));
            String input = sc.nextLine().trim();
            try {
                double rate = Double.parseDouble(input);
                if (rate > 0) {
                    return rate;
                }
                TerminalUI.printCentered("Interest rate must be greater than 0.");
            } catch (NumberFormatException e) {
                TerminalUI.printCentered(">>> [Invalid Input] Please enter a valid number.");
            }
        }
    }

    private void printFullAccountData(BankAccountRecord bankAccount,
                                      AccountDetails profile,
                                      UserCredentials user,
                                      ArrayList<Transaction> transactions) {
        double totalDeposited = 0.0;
        double totalWithdrawn = 0.0;
        int depositCount = 0;
        int withdrawCount = 0;

        for (Transaction transaction : transactions) {
            if (!bankAccount.getAccountNumber().equals(transaction.getAccountNumber())) {
                continue;
            }
            if (bankAccount.getUserUUID() != null
                    && transaction.getUserUUID() != null
                    && !bankAccount.getUserUUID().equals(transaction.getUserUUID())) {
                continue;
            }

            if ("Deposit".equalsIgnoreCase(transaction.getType())) {
                totalDeposited += transaction.getAmount();
                depositCount++;
            } else if ("Withdraw".equalsIgnoreCase(transaction.getType())) {
                totalWithdrawn += transaction.getAmount();
                withdrawCount++;
            }
        }

        TerminalUI.printCentered("╔════════════════════════════════════════════╗");
        TerminalUI.printCentered("║             FULL ACCOUNT DATA              ║");
        TerminalUI.printCentered("╚════════════════════════════════════════════╝");

        TerminalUI.printCentered("[Bank Account Record]");
        TerminalUI.printCentered("User UUID                  : " + valueOrNA(bankAccount.getUserUUID()));
        TerminalUI.printCentered("Linked Username            : " + valueOrNA(bankAccount.getUsername()));
        TerminalUI.printCentered("Linked Profile Account No. : " + valueOrNA(bankAccount.getLinkedProfileAccountNumber()));
        TerminalUI.printCentered("Account Number             : " + valueOrNA(bankAccount.getAccountNumber()));
        TerminalUI.printCentered("Account Type               : " + valueOrNA(bankAccount.getAccountType()));
        TerminalUI.printCentered(String.format("Balance                    : RM %.2f", bankAccount.getBalance()));
        TerminalUI.printCentered(String.format("Interest Rate              : %.2f%%", bankAccount.getInterestRate()));
        TerminalUI.printCentered(String.format("Overdraft Limit            : RM %.2f", bankAccount.getOverdraftLimit()));
        TerminalUI.printCentered("Frozen                     : " + bankAccount.isFrozen());

        TerminalUI.printCentered("[Account Profile]");
        if (profile == null) {
            TerminalUI.printCentered("Profile                    : Not found");
        } else {
            TerminalUI.printCentered("UUID                       : " + valueOrNA(profile.getUUID()));
            TerminalUI.printCentered("Name                       : " + valueOrNA(profile.getName()));
            TerminalUI.printCentered("Age                        : " + profile.getAge());
            TerminalUI.printCentered("Date of Birth              : " + valueOrNA(profile.getDOB()));
            TerminalUI.printCentered("MyKad                      : " + valueOrNA(profile.getMykad()));
            TerminalUI.printCentered("Gender                     : " + (profile.getGender() == '\0' ? "N/A" : profile.getGender()));
            TerminalUI.printCentered("Username                   : " + valueOrNA(profile.getUsername()));
            TerminalUI.printCentered("Profile Account Number     : " + valueOrNA(profile.getAccountNumber()));
            TerminalUI.printCentered("Profile Account Type       : " + valueOrNA(profile.getAccountType()));
        }

        TerminalUI.printCentered("[User Credentials]");
        if (user == null) {
            TerminalUI.printCentered("Credentials                : Not found");
        } else {
            TerminalUI.printCentered("Username                   : " + valueOrNA(user.getUsername()));
            TerminalUI.printCentered("Password                   : " + valueOrNA(user.getPassword()));
            TerminalUI.printCentered("UUID                       : " + valueOrNA(user.getUUID()));
        }

        TerminalUI.printCentered("[Transaction Summary]");
        TerminalUI.printCentered(String.format("Total Deposited            : RM %.2f (%d transactions)", totalDeposited, depositCount));
        TerminalUI.printCentered(String.format("Total Withdrawn            : RM %.2f (%d transactions)", totalWithdrawn, withdrawCount));
    }

    private String valueOrNA(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value;
    }

    private void printTransactionHistory(BankAccountRecord bankAccount, ArrayList<Transaction> transactions) {
        TerminalUI.printCentered("╔════════════════════════════════════════════╗");
        TerminalUI.printCentered("║         FULL TRANSACTION HISTORY           ║");
        TerminalUI.printCentered("╠════════════════════════════════════════════╣");
        TerminalUI.printCentered("Account Number : " + valueOrNA(bankAccount.getAccountNumber()));
        TerminalUI.printCentered("Account Type   : " + valueOrNA(bankAccount.getAccountType()));
        TerminalUI.printCentered("╠════════════════════════════════════════════╣");

        int itemNo = 0;
        for (Transaction transaction : transactions) {
            if (!isTransactionForAccount(bankAccount, transaction)) {
                continue;
            }
            itemNo++;
            TerminalUI.printCentered(String.format("%d. [%s] %s RM %.2f (Ref: %s)",
                    itemNo,
                    valueOrNA(transaction.getTimestamp()),
                    valueOrNA(transaction.getType()),
                    transaction.getAmount(),
                    valueOrNA(transaction.getReferenceID())));
        }

        if (itemNo == 0) {
            TerminalUI.printCentered("No transactions found for this account.");
        }
        TerminalUI.printCentered("╚════════════════════════════════════════════╝");
    }

    private boolean isTransactionForAccount(BankAccountRecord bankAccount, Transaction transaction) {
        if (!bankAccount.getAccountNumber().equals(transaction.getAccountNumber())) {
            return false;
        }
        if (bankAccount.getUserUUID() != null
                && transaction.getUserUUID() != null
                && !bankAccount.getUserUUID().equals(transaction.getUserUUID())) {
            return false;
        }
        return true;
    }
}
