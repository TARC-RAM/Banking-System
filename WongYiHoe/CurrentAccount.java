public class CurrentAccount extends Account {

  public CurrentAccount(String customerID, double initialBalance) {
    super(customerID, initialBalance);
  }

  public CurrentAccount(String customerID, String accountNumber, double initialBalance) {
    super(customerID, accountNumber, initialBalance);
  }

  // Overdraft limit is 50% of the current positive balance
  public double getOverdraftLimit() {
    if (getBalance() > 0) {
      return getBalance() * 0.5;
    } else {
      return 0.0; // No overdraft if balance is zero or negative
    }
  }

  @Override
  public void performMonthlyProcess() {
    if (getIsFrozen()) {
      System.out.println("Monthly Process Failed: Account is frozen.");
      return;
    }

    if (getBalance() < 0) {
      double interestCharge = Math.abs(getBalance()) * 0.01;
      setBalance(getBalance() - interestCharge);

      System.out.printf("Monthly Interest Charge (1%%): -RM %.2f\n", interestCharge);
      System.out.printf("Warning: Account in overdraft. New Debt: RM %.2f\n", Math.abs(getBalance()));
    } else {
      System.out.println("❖ Monthly Process: Current Account status healthy.");
    }
  }

  @Override
  public boolean withdraw(double amount) {
    if (getIsFrozen()) {
      System.out.println("Withdraw Failed: Account is frozen.");
      return false;
    }

    // Calculate available funds including overdraft
    double currentLimit = getOverdraftLimit();
    double availableFunds = getBalance() + currentLimit;

    if (amount <= availableFunds) {
      setBalance(getBalance() - amount);
      System.out.println("Withdraw Successful\t: RM " + amount);

      if (getBalance() < 0) {
        System.out.printf("Warning !: Using Overdraft\t: RM %.2f\n", Math.abs(getBalance()));
      }
      return true;
    } else {
      System.out.println("Withdraw Failed: Exceeds overdraft limit.");
      System.out.printf("Maximum amount available to withdraw is\t: RM %.2f\n", availableFunds);
      return false;
    }
  }
}
