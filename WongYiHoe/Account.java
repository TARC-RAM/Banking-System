public abstract class Account {
    private static int savingsCounter = 1000;
    private static int currentCounter = 1000;
    
    private String accountNumber;
    private double balance;
    private String userUUID;
    private String customerID;
    private boolean isFrozen;

    public Account(String userUUID, String customerID, double initialBalance) {
        this(userUUID, customerID, initialBalance, "CA");
    }

    public Account(String userUUID, String customerID, double initialBalance, String accountPrefix) {
        this(userUUID, customerID, generateAccountNumber(accountPrefix), initialBalance);
    }

    public Account(String userUUID, String customerID, String accountNumber, double initialBalance) {
        syncCounterWith(accountNumber);
        this.accountNumber = accountNumber;
        this.userUUID = userUUID;
        this.customerID = customerID;
        this.balance = initialBalance;
        this.isFrozen = false;
    }


    // Getters and Setters
    public String getAccountNumber() { 
        return accountNumber; }

    public double getBalance() { 
        return balance; }

    public String getUserUUID() {
        return userUUID;
    }

    public String getCustomerID() { 
        return customerID; }

    public boolean getIsFrozen() { 
        return isFrozen; }

    // Protected setter for balance to allow subclasses to modify it
    protected void setBalance(double balance) { 
        this.balance = balance; }

    public void setFrozen(boolean frozen) { 
        isFrozen = frozen; }

    private static String generateAccountNumber(String accountPrefix) {
        if ("SA".equals(accountPrefix)) {
            return "SA" + (savingsCounter++);
        }
        return "CA" + (currentCounter++);
    }

    private static void syncCounterWith(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 3) {
            return;
        }
        String prefix = accountNumber.substring(0, 2);
        String suffix = accountNumber.substring(2);

        if ("ACC".equals(accountNumber.substring(0, 3))) {
            suffix = accountNumber.substring(3);
            prefix = "SA";
        }

        if (!suffix.matches("\\d+")) {
            return;
        }
        int usedNumber = Integer.parseInt(suffix);

        if ("SA".equals(prefix) && usedNumber >= savingsCounter) {
            savingsCounter = usedNumber + 1;
        } else if ("CA".equals(prefix) && usedNumber >= currentCounter) {
            currentCounter = usedNumber + 1;
        }
    }


    public abstract void performMonthlyProcess();

    // deposit
    public void deposit(double amount) {
        if (!isFrozen && amount > 0) {
            balance += amount;
            TerminalUI.printCentered("Deposit Successful: RM " + amount);
        } else {
            TerminalUI.printCentered("Deposit Failed: Account is frozen or amount is invalid.");
        }
    }

    // withdraw
    public boolean withdraw(double amount) {
        if (isFrozen) {
            TerminalUI.printCentered("Withdraw Failed: Account is frozen.");
            return false;
        }
        if (amount <= balance) {
            balance -= amount;
            TerminalUI.printCentered("Withdraw Successful: RM " + amount);
            return true;
        } else {
            TerminalUI.printCentered("Withdraw Failed: Insufficient funds.");
            return false;
        }
    }
}
