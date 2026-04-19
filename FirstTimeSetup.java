import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstTimeSetup {
  private static final int MAX_ACCOUNTS_PER_USER = 3;

  private static class AccountSelection {
    private final AccountDetails profile;
    private final BankAccountRecord bankRecord;

    private AccountSelection(AccountDetails profile, BankAccountRecord bankRecord) {
      this.profile = profile;
      this.bankRecord = bankRecord;
    }
  }

  Scanner scanner;
  FileManager fm = new FileManager();

  public FirstTimeSetup() {
    this(new Scanner(System.in));
  }

  public FirstTimeSetup(Scanner scanner) {
    this.scanner = scanner;
  }

  public void setup(UserCredentials user) {
    ArrayList<AccountDetails> accountProfiles = fm.loadAccounts();
    ArrayList<BankAccountRecord> bankAccounts = fm.loadBankAccounts();

    ArrayList<AccountDetails> userProfiles = findProfilesByUser(user, accountProfiles);
    if (userProfiles.isEmpty()) {
      AccountDetails profile = createInitialProfile(user, accountProfiles, bankAccounts);
      accountProfiles.add(profile);
      fm.saveAccounts(accountProfiles);
      TerminalUI.printCentered("Profile saved. Account linked successfully.");

      BankAccountRecord bankRecord = createBankRecord(profile, user);
      bankAccounts.add(bankRecord);
      fm.saveBankAccounts(bankAccounts);
      runAccountMenu(user, profile, bankRecord, bankAccounts);
      return;
    }

    createMissingBankRecords(user, userProfiles, bankAccounts);
    AccountSelection selection = selectAccountForSession(user, userProfiles, accountProfiles, bankAccounts);
    if (selection != null) {
      runAccountMenu(user, selection.profile, selection.bankRecord, bankAccounts);
    }
  }

  private ArrayList<AccountDetails> findProfilesByUser(UserCredentials user, ArrayList<AccountDetails> accountProfiles) {
    ArrayList<AccountDetails> userProfiles = new ArrayList<>();
    for (AccountDetails profile : accountProfiles) {
      if (user.getUUID().equals(profile.getUUID())) {
        userProfiles.add(profile);
      }
    }
    return userProfiles;
  }

  private BankAccountRecord findBankRecordForProfile(UserCredentials user, AccountDetails profile,
      ArrayList<BankAccountRecord> bankAccounts) {
    for (BankAccountRecord record : bankAccounts) {
      if (user.getUUID().equals(record.getUserUUID())
          && profile.getAccountNumber().equals(record.getLinkedProfileAccountNumber())) {
        return record;
      }
    }
    return null;
  }

  private void createMissingBankRecords(UserCredentials user,
                                        ArrayList<AccountDetails> userProfiles,
                                        ArrayList<BankAccountRecord> bankAccounts) {
    boolean changed = false;
    for (AccountDetails profile : userProfiles) {
      BankAccountRecord existingRecord = findBankRecordForProfile(user, profile, bankAccounts);
      if (existingRecord == null) {
        bankAccounts.add(createBankRecord(profile, user));
        changed = true;
      }
    }
    if (changed) {
      fm.saveBankAccounts(bankAccounts);
    }
  }

  private AccountSelection selectAccountForSession(UserCredentials user,
                                                    ArrayList<AccountDetails> userProfiles,
                                                    ArrayList<AccountDetails> accountProfiles,
                                                    ArrayList<BankAccountRecord> bankAccounts) {
    while (true) {
      ArrayList<AccountSelection> linkedAccounts = new ArrayList<>();
      for (AccountDetails profile : userProfiles) {
        BankAccountRecord record = findBankRecordForProfile(user, profile, bankAccounts);
        if (record != null) {
          linkedAccounts.add(new AccountSelection(profile, record));
        }
      }

      TerminalUI.clearScreen();
      printHeader("SELECT ACCOUNT");
      for (int i = 0; i < linkedAccounts.size(); i++) {
        AccountSelection account = linkedAccounts.get(i);
        String label = String.format("[%d] %s (%s)", i + 1,
            account.bankRecord.getAccountNumber(),
            account.bankRecord.getAccountType());
        TerminalUI.printCentered(label);
      }

      int createOption = linkedAccounts.size() + 1;
      if (linkedAccounts.size() < MAX_ACCOUNTS_PER_USER) {
        TerminalUI.printCentered("[" + createOption + "] Create New Account");
      }
      TerminalUI.printCentered("[0] Logout to Main Menu");

      int choice = readMenuChoice("Please enter your choice: ");
      if (choice == 0) {
        return null;
      }
      if (choice >= 1 && choice <= linkedAccounts.size()) {
        return linkedAccounts.get(choice - 1);
      }
      if (linkedAccounts.size() < MAX_ACCOUNTS_PER_USER && choice == createOption) {
        AccountDetails newProfile = createAdditionalProfile(user, userProfiles.get(0), accountProfiles, bankAccounts);
        accountProfiles.add(newProfile);
        userProfiles.add(newProfile);
        fm.saveAccounts(accountProfiles);

        BankAccountRecord newRecord = createBankRecord(newProfile, user);
        bankAccounts.add(newRecord);
        fm.saveBankAccounts(bankAccounts);
        TerminalUI.printCentered("New account created successfully.");
        TerminalUI.pauseForEnter(scanner);
        return new AccountSelection(newProfile, newRecord);
      }
      TerminalUI.printCentered("Invalid selection, please try again.");
    }
  }

  private AccountDetails createInitialProfile(UserCredentials user,
                                              ArrayList<AccountDetails> accountProfiles,
                                              ArrayList<BankAccountRecord> bankAccounts) {
    AccountDetails profile = new AccountDetails();

    TerminalUI.clearScreen();
    printHeader("PROFILE SETUP");
    profile.setName(readName());
    profile.setAge(readPositiveInt("Enter your Age: "));
    profile.setDOB(readDOB());
    profile.setMykad(readMykad());
    profile.setGender(readGender());
    profile.setUsername(user.getUsername());
    profile.setUUID(user.getUUID());
    profile.setAccountType(readAccountTypeChoice());
    profile.setAccountNumber(generateNextAccountNumber(accountProfiles, bankAccounts));

    return profile;
  }

  private AccountDetails createAdditionalProfile(UserCredentials user,
                                                 AccountDetails templateProfile,
                                                 ArrayList<AccountDetails> accountProfiles,
                                                 ArrayList<BankAccountRecord> bankAccounts) {
    AccountDetails profile = new AccountDetails();
    profile.setName(templateProfile.getName());
    profile.setAge(templateProfile.getAge());
    profile.setDOB(templateProfile.getDOB());
    profile.setMykad(templateProfile.getMykad());
    profile.setGender(templateProfile.getGender());
    profile.setUsername(user.getUsername());
    profile.setUUID(user.getUUID());
    profile.setAccountType(readAccountTypeChoice());
    profile.setAccountNumber(generateNextAccountNumber(accountProfiles, bankAccounts));
    return profile;
  }

  private BankAccountRecord createBankRecord(AccountDetails profile, UserCredentials user) {
    BankAccountRecord record = new BankAccountRecord();
    record.setUserUUID(user.getUUID());
    record.setUsername(user.getUsername());
    record.setLinkedProfileAccountNumber(profile.getAccountNumber());
    record.setAccountNumber(profile.getAccountNumber());
    record.setAccountType(profile.getAccountType());
    record.setBalance(0.0);
    if ("Savings".equalsIgnoreCase(profile.getAccountType())) {
      record.setInterestRate(2.5);
      record.setOverdraftLimit(0.0);
    } else {
      record.setInterestRate(0.0);
      record.setOverdraftLimit(0.0);
    }
    record.setFrozen(false);
    return record;
  }

  private String readAccountTypeChoice() {
    while (true) {
      TerminalUI.clearScreen();
      printHeader("SELECT ACCOUNT TYPE");
      TerminalUI.printCentered("[1] Savings Account");
      TerminalUI.printCentered("[2] Current Account");
      int choice = readMenuChoice("Please enter (1 / 2): ");

      if (choice == 1) {
        return "Savings";
      }
      if (choice == 2) {
        return "Current";
      }
      TerminalUI.printCentered("Invalid account type. Please enter 1 or 2.");
    }
  }

  private String generateNextAccountNumber(ArrayList<AccountDetails> accountProfiles,
                                           ArrayList<BankAccountRecord> bankAccounts) {
    int maxAccountNumber = 999;

    for (AccountDetails profile : accountProfiles) {
      maxAccountNumber = Math.max(maxAccountNumber, extractAccountNumberValue(profile.getAccountNumber()));
    }
    for (BankAccountRecord record : bankAccounts) {
      maxAccountNumber = Math.max(maxAccountNumber, extractAccountNumberValue(record.getLinkedProfileAccountNumber()));
      maxAccountNumber = Math.max(maxAccountNumber, extractAccountNumberValue(record.getAccountNumber()));
    }

    return "ACC" + (maxAccountNumber + 1);
  }

  private int extractAccountNumberValue(String accountNumber) {
    if (accountNumber == null) {
      return -1;
    }
    Matcher matcher = Pattern.compile("(\\d+)$").matcher(accountNumber.trim());
    if (!matcher.find()) {
      return -1;
    }
    try {
      return Integer.parseInt(matcher.group(1));
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private Account restoreAccount(BankAccountRecord record) {
    Account restoredAccount;
    if ("Savings".equalsIgnoreCase(record.getAccountType())) {
      double interestRate = record.getInterestRate() > 0 ? record.getInterestRate() : 2.5;
      restoredAccount = new SavingsAccount(
          record.getUserUUID(),
          record.getAccountNumber(),
          record.getAccountNumber(),
          record.getBalance(),
          interestRate);
    } else {
      restoredAccount = new CurrentAccount(
          record.getUserUUID(),
          record.getAccountNumber(),
          record.getAccountNumber(),
          record.getBalance());
    }
    restoredAccount.setFrozen(record.isFrozen());
    return restoredAccount;
  }

  private void runAccountMenu(UserCredentials user, AccountDetails profile, BankAccountRecord record,
                              ArrayList<BankAccountRecord> bankAccounts) {
    AccountDetails activeProfile = profile;
    BankAccountRecord activeRecord = record;
    Account activeAccount = restoreAccount(activeRecord);
    ArrayList<Transaction> transactions = fm.loadTransactions();
    boolean running = true;

    while (running) {
      TerminalUI.clearScreen();
      printHeader("ACCOUNT DASHBOARD");
      TerminalUI.printCentered("User           : " + activeProfile.getName());
      TerminalUI.printCentered("Account Number : " + activeRecord.getAccountNumber());
      TerminalUI.printCentered("Account Type   : " + activeRecord.getAccountType());
      TerminalUI.printCentered("Balance        : RM " + String.format("%.2f", activeAccount.getBalance()));
      TerminalUI.printCentered("");
      TerminalUI.printCentered("[1] Deposit Funds");
      TerminalUI.printCentered("[2] Withdraw Funds");
      TerminalUI.printCentered("[3] Check Balance");
      TerminalUI.printCentered("[4] Run Monthly Process");
      TerminalUI.printCentered("[5] Create Another Account");
      TerminalUI.printCentered("[0] Logout to Main Menu");

      int choice = readMenuChoice("Please enter your choice: ");
      boolean shouldPause = true;

      switch (choice) {
        case 1:
          double depAmount = readPositiveDouble("Deposit amount (RM): ");
          double preDepositBalance = activeAccount.getBalance();
          activeAccount.deposit(depAmount);
          if (activeAccount.getBalance() > preDepositBalance) {
            recordTransaction(transactions, activeAccount, "Deposit", depAmount, "DEP");
          }
          syncRecord(activeRecord, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 2:
          if (activeAccount instanceof CurrentAccount) {
            CurrentAccount current = (CurrentAccount) activeAccount;
            TerminalUI.printCentered(String.format("Available Funds : RM %.2f", current.getBalance() + current.getOverdraftLimit()));
          } else {
            TerminalUI.printCentered(String.format("Available Funds : RM %.2f", activeAccount.getBalance()));
          }
          double withAmount = readPositiveDouble("Withdraw amount (RM): ");
          boolean withdrawSuccess = activeAccount.withdraw(withAmount);
          if (withdrawSuccess) {
            recordTransaction(transactions, activeAccount, "Withdraw", withAmount, "WDL");
          }
          syncRecord(activeRecord, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 3:
          printBalance(activeAccount);
          break;
        case 4:
          double preMonthlyBalance = activeAccount.getBalance();
          activeAccount.performMonthlyProcess();
          double monthlyDelta = activeAccount.getBalance() - preMonthlyBalance;
          if (monthlyDelta > 0) {
            recordTransaction(transactions, activeAccount, "Monthly Interest", monthlyDelta, "MIP");
          } else if (monthlyDelta < 0) {
            recordTransaction(transactions, activeAccount, "Monthly Charge", Math.abs(monthlyDelta), "MCH");
          }
          syncRecord(activeRecord, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 5:
          ArrayList<AccountDetails> accountProfiles = fm.loadAccounts();
          ArrayList<AccountDetails> userProfiles = findProfilesByUser(user, accountProfiles);
          if (userProfiles.size() >= MAX_ACCOUNTS_PER_USER) {
            TerminalUI.printCentered("Maximum of 3 accounts reached.");
            break;
          }
          AccountDetails newProfile = createAdditionalProfile(user, userProfiles.get(0), accountProfiles, bankAccounts);
          accountProfiles.add(newProfile);
          fm.saveAccounts(accountProfiles);

          BankAccountRecord newRecord = createBankRecord(newProfile, user);
          bankAccounts.add(newRecord);
          fm.saveBankAccounts(bankAccounts);

          activeProfile = newProfile;
          activeRecord = newRecord;
          activeAccount = restoreAccount(activeRecord);
          TerminalUI.printCentered("New account created and selected.");
          break;
        case 0:
          running = false;
          shouldPause = false;
          TerminalUI.printCentered("Returning to main menu...");
          break;
        default:
          TerminalUI.printCentered("Invalid selection, please try again.");
      }

      if (running && shouldPause) {
        TerminalUI.pauseForEnter(scanner);
      }
    }
  }

  private void syncRecord(BankAccountRecord record, Account account) {
    record.setBalance(account.getBalance());
    record.setFrozen(account.getIsFrozen());
    if (account instanceof SavingsAccount) {
      SavingsAccount savings = (SavingsAccount) account;
      record.setInterestRate(savings.getInterestRate());
      record.setOverdraftLimit(0.0);
    } else if (account instanceof CurrentAccount) {
      CurrentAccount current = (CurrentAccount) account;
      record.setInterestRate(0.0);
      record.setOverdraftLimit(current.getOverdraftLimit());
    }
  }

  private void recordTransaction(ArrayList<Transaction> transactions, Account account,
                                 String type, double amount, String referencePrefix) {
    String reference = referencePrefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    transactions.add(new Transaction(type, amount, reference, account.getUserUUID(), account.getAccountNumber()));
    fm.saveTransactions(transactions);
  }

  private void printBalance(Account account) {
    printHeader("BALANCE DETAILS");
    TerminalUI.printCentered(String.format("Current Balance : RM %.2f", account.getBalance()));
    if (account instanceof CurrentAccount) {
      CurrentAccount current = (CurrentAccount) account;
      TerminalUI.printCentered(String.format("Overdraft Limit : RM %.2f", current.getOverdraftLimit()));
      TerminalUI.printCentered(String.format("Total Available : RM %.2f", current.getBalance() + current.getOverdraftLimit()));
    } else if (account instanceof SavingsAccount) {
      SavingsAccount savings = (SavingsAccount) account;
      TerminalUI.printCentered(String.format("Interest Rate   : %.2f%%", savings.getInterestRate()));
    }
    TerminalUI.printCentered("╚════════════════════════════════════════════╝");
  }

  private void printHeader(String title) {
    TerminalUI.printCentered("╔════════════════════════════════════════════╗");
    TerminalUI.printCentered(String.format("║ %-42s ║", title));
    TerminalUI.printCentered("╚════════════════════════════════════════════╝");
  }

  private int readMenuChoice(String prompt) {
    while (true) {
      System.out.print(TerminalUI.menuPrompt(prompt));
      String input = scanner.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        TerminalUI.printCentered("Please enter a valid number.");
      }
    }
  }

  private int readPositiveInt(String prompt) {
    while (true) {
      int value = readMenuChoice(prompt);
      if (value > 0) {
        return value;
      }
      TerminalUI.printCentered("Value must be a positive number.");
    }
  }

  private double readPositiveDouble(String prompt) {
    while (true) {
      System.out.print(TerminalUI.menuPrompt(prompt));
      String input = scanner.nextLine().trim();
      try {
        double value = Double.parseDouble(input);
        if (value > 0) {
          return value;
        }
        TerminalUI.printCentered("Amount must be greater than 0.");
      } catch (NumberFormatException e) {
        TerminalUI.printCentered("Please enter a valid amount.");
      }
    }
  }

  private String readName() {
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your Name: "));
      String name = scanner.nextLine().trim();
      if (name.matches("[A-Za-z ]+")) {
        return name;
      }
      TerminalUI.printCentered("Name can only contain letters and spaces.");
    }
  }

  private String readDOB() {
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your Date of Birth (DD/MM/YYYY): "));
      String dob = scanner.nextLine().trim();
      if (dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
        return dob;
      }
      TerminalUI.printCentered("DOB must be in DD/MM/YYYY format.");
    }
  }

  private String readMykad() {
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your MyKad: "));
      String mykad = scanner.nextLine().trim();
      if (mykad.matches("\\d+")) {
        return mykad;
      }
      TerminalUI.printCentered("MyKad must be digits only.");
    }
  }

  private char readGender() {
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Select your Gender (1 = Male, 2 = Female): "));
      String input = scanner.nextLine().trim();
      if ("1".equals(input)) {
        return 'M';
      }
      if ("2".equals(input)) {
        return 'F';
      }
      TerminalUI.printCentered("Please enter 1 for Male or 2 for Female.");
    }
  }
}
