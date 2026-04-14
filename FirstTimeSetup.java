import java.util.ArrayList;
import java.util.Scanner;

public class FirstTimeSetup {
  Scanner scanner = new Scanner(System.in);
  FileManager fm = new FileManager();

  public void setup(UserCredentials user) {
    ArrayList<AccountDetails> accountProfiles = fm.loadAccounts();
    ArrayList<BankAccountRecord> bankAccounts = fm.loadBankAccounts();

    AccountDetails profile = findProfile(user, accountProfiles);
    if (profile == null) {
      profile = createProfile(user);
      accountProfiles.add(profile);
      fm.saveAccounts(accountProfiles);
      System.out.println("Profile saved. Account linked successfully.");
    }

    BankAccountRecord bankRecord = findBankRecord(user, profile, bankAccounts);
    if (bankRecord == null) {
      bankRecord = createBankRecord(profile, user);
      bankAccounts.add(bankRecord);
      fm.saveBankAccounts(bankAccounts);
    }

    runAccountMenu(profile, bankRecord, bankAccounts);
  }

  private AccountDetails findProfile(UserCredentials user, ArrayList<AccountDetails> accountProfiles) {
    for (AccountDetails profile : accountProfiles) {
      if (user.getUUID().equals(profile.getUUID())) {
        return profile;
      }
    }
    return null;
  }

  private BankAccountRecord findBankRecord(UserCredentials user, AccountDetails profile,
      ArrayList<BankAccountRecord> bankAccounts) {
    for (BankAccountRecord record : bankAccounts) {
      if (user.getUUID().equals(record.getUserUUID())
          && profile.getAccountNumber().equals(record.getLinkedProfileAccountNumber())) {
        return record;
      }
    }
    return null;
  }

  private AccountDetails createProfile(UserCredentials user) {
    AccountDetails profile = new AccountDetails();

    printHeader("PROFILE SETUP");
    profile.setName(readName());
    profile.setAge(readPositiveInt("Enter your Age: "));
    profile.setDOB(readDOB());
    profile.setMykad(readMykad());
    profile.setGender(readGender());
    profile.setUsername(user.getUsername());
    profile.setUUID(user.getUUID());

    Account seedAccount = createNewAccountByChoice(user.getUUID());
    profile.setAccountType(seedAccount instanceof SavingsAccount ? "Savings" : "Current");
    profile.setAccountNumber(seedAccount.getAccountNumber());

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
    return record;
  }

  private Account createNewAccountByChoice(String userUUID) {
    while (true) {
      System.out.println("\nSelect Account Type:");
      System.out.println("  [1] Savings Account");
      System.out.println("  [2] Current Account");
      int choice = readMenuChoice("Please enter (1 / 2): ");

      if (choice == 1) {
        return new SavingsAccount(userUUID, 0.0, 2.5);
      }
      if (choice == 2) {
        return new CurrentAccount(userUUID, 0.0);
      }
      System.out.println("Invalid account type. Please enter 1 or 2.");
    }
  }

  private Account restoreAccount(BankAccountRecord record) {
    if ("Savings".equalsIgnoreCase(record.getAccountType())) {
      double interestRate = record.getInterestRate() > 0 ? record.getInterestRate() : 2.5;
      return new SavingsAccount(
          record.getUserUUID(),
          record.getAccountNumber(),
          record.getBalance(),
          interestRate);
    }
    return new CurrentAccount(
        record.getUserUUID(),
        record.getAccountNumber(),
        record.getBalance());
  }

  private void runAccountMenu(AccountDetails profile, BankAccountRecord record, ArrayList<BankAccountRecord> bankAccounts) {
    Account activeAccount = restoreAccount(record);
    boolean running = true;

    while (running) {
      printHeader("ACCOUNT DASHBOARD");
      System.out.println("User           : " + profile.getName());
      System.out.println("Account Number : " + record.getAccountNumber());
      System.out.println("Account Type   : " + record.getAccountType());
      System.out.println("Balance        : RM " + String.format("%.2f", activeAccount.getBalance()));
      System.out.println();
      System.out.println("  [1] Deposit Funds");
      System.out.println("  [2] Withdraw Funds");
      System.out.println("  [3] Check Balance");
      System.out.println("  [4] Run Monthly Process");
      System.out.println("  [0] Logout to Main Menu");

      int choice = readMenuChoice("Please enter your choice: ");

      switch (choice) {
        case 1:
          double depAmount = readPositiveDouble("Deposit amount (RM): ");
          activeAccount.deposit(depAmount);
          syncRecord(record, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 2:
          if (activeAccount instanceof CurrentAccount) {
            CurrentAccount current = (CurrentAccount) activeAccount;
            System.out.printf("Available Funds : RM %.2f%n", current.getBalance() + current.getOverdraftLimit());
          } else {
            System.out.printf("Available Funds : RM %.2f%n", activeAccount.getBalance());
          }
          double withAmount = readPositiveDouble("Withdraw amount (RM): ");
          activeAccount.withdraw(withAmount);
          syncRecord(record, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 3:
          printBalance(activeAccount);
          break;
        case 4:
          activeAccount.performMonthlyProcess();
          syncRecord(record, activeAccount);
          fm.saveBankAccounts(bankAccounts);
          break;
        case 0:
          running = false;
          System.out.println("Returning to main menu...");
          break;
        default:
          System.out.println("Invalid selection, please try again.");
      }
    }
  }

  private void syncRecord(BankAccountRecord record, Account account) {
    record.setBalance(account.getBalance());
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

  private void printBalance(Account account) {
    System.out.println("\n--------------------------------------");
    System.out.printf("Current Balance : RM %.2f%n", account.getBalance());
    if (account instanceof CurrentAccount) {
      CurrentAccount current = (CurrentAccount) account;
      System.out.printf("Overdraft Limit : RM %.2f%n", current.getOverdraftLimit());
      System.out.printf("Total Available : RM %.2f%n", current.getBalance() + current.getOverdraftLimit());
    } else if (account instanceof SavingsAccount) {
      SavingsAccount savings = (SavingsAccount) account;
      System.out.printf("Interest Rate   : %.2f%%%n", savings.getInterestRate());
    }
    System.out.println("--------------------------------------\n");
  }

  private void printHeader(String title) {
    System.out.println("\n╔════════════════════════════════════════════╗");
    System.out.printf("║ %-42s ║%n", title);
    System.out.println("╚════════════════════════════════════════════╝");
  }

  private int readMenuChoice(String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number.");
      }
    }
  }

  private int readPositiveInt(String prompt) {
    while (true) {
      int value = readMenuChoice(prompt);
      if (value > 0) {
        return value;
      }
      System.out.println("Value must be a positive number.");
    }
  }

  private double readPositiveDouble(String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      try {
        double value = Double.parseDouble(input);
        if (value > 0) {
          return value;
        }
        System.out.println("Amount must be greater than 0.");
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid amount.");
      }
    }
  }

  private String readName() {
    while (true) {
      System.out.print("Enter your Name: ");
      String name = scanner.nextLine().trim();
      if (name.matches("[A-Za-z ]+")) {
        return name;
      }
      System.out.println("Name can only contain letters and spaces.");
    }
  }

  private String readDOB() {
    while (true) {
      System.out.print("Enter your Date of Birth (DD/MM/YYYY): ");
      String dob = scanner.nextLine().trim();
      if (dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
        return dob;
      }
      System.out.println("DOB must be in DD/MM/YYYY format.");
    }
  }

  private String readMykad() {
    while (true) {
      System.out.print("Enter your MyKad: ");
      String mykad = scanner.nextLine().trim();
      if (mykad.matches("\\d+")) {
        return mykad;
      }
      System.out.println("MyKad must be digits only.");
    }
  }

  private char readGender() {
    while (true) {
      System.out.print("Select your Gender (1 = Male, 2 = Female): ");
      String input = scanner.nextLine().trim();
      if ("1".equals(input)) {
        return 'M';
      }
      if ("2".equals(input)) {
        return 'F';
      }
      System.out.println("Please enter 1 for Male or 2 for Female.");
    }
  }
}
