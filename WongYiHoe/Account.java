public abstract class Account {
    private static int counter = 1000;
    
    private String accountNumber;
    private double balance;
    private String customerID;
    private boolean isFrozen;

    public Account(String customerID, double initialBalance) {
        this(customerID, "ACC" + (counter++), initialBalance);
    }

    public Account(String customerID, String accountNumber, double initialBalance) {
        syncCounterWith(accountNumber);
        this.accountNumber = accountNumber;
        this.customerID = customerID;
        this.balance = initialBalance;
        this.isFrozen = false;
    }


    // Getters and Setters
    public String getAccountNumber() { 
        return accountNumber; }

    public double getBalance() { 
        return balance; }

    public String getCustomerID() { 
        return customerID; }

    public boolean getIsFrozen() { 
        return isFrozen; }

    // Protected setter for balance to allow subclasses to modify it
    protected void setBalance(double balance) { 
        this.balance = balance; }

    public void setFrozen(boolean frozen) { 
        isFrozen = frozen; }

    private static void syncCounterWith(String accountNumber) {
        if (accountNumber == null || !accountNumber.startsWith("ACC")) {
            return;
        }
        String suffix = accountNumber.substring(3);
        if (!suffix.matches("\\d+")) {
            return;
        }
        int usedNumber = Integer.parseInt(suffix);
        if (usedNumber >= counter) {
            counter = usedNumber + 1;
        }
    }


    public abstract void performMonthlyProcess();

    // deposit
    public void deposit(double amount) {
        if (!isFrozen && amount > 0) {
            balance += amount;
            System.out.println("Deposit Successful: RM " + amount);
        } else {
            System.out.println("Deposit Failed: Account is frozen or amount is invalid.");
        }
    }

    // withdraw
    public boolean withdraw(double amount) {
        if (isFrozen) {
            System.out.println("Withdraw Failed: Account is frozen.");
            return false;
        }
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdraw Successful: RM " + amount);
            return true;
        } else {
            System.out.println("Withdraw Failed: Insufficient funds.");
            return false;
        }
    }
}
