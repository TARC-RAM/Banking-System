public class CurrentAccount extends Account {

  public CurrentAccount(String userUUID, String customerID, double initialBalance) {
    super(userUUID, customerID, initialBalance, "CA");
  }

  public CurrentAccount(String userUUID, String customerID, String accountNumber, double initialBalance) {
    super(userUUID, customerID, accountNumber, initialBalance);
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
      TerminalUI.printCentered("Monthly Process Failed: Account is frozen.");
      return;
    }

    if (getBalance() < 0) {
      double interestCharge = Math.abs(getBalance()) * 0.01;
      setBalance(getBalance() - interestCharge);

      TerminalUI.printCentered(String.format("Monthly Interest Charge (1%%): -RM %.2f", interestCharge));
      TerminalUI.printCentered(String.format("Warning: Account in overdraft. New Debt: RM %.2f", Math.abs(getBalance())));
    } else {
      TerminalUI.printCentered("Monthly Process: Current Account status healthy.");
    }
  }

  @Override
  public boolean withdraw(double amount) {
    if (getIsFrozen()) {
      TerminalUI.printCentered("Withdraw Failed: Account is frozen.");
      return false;
    }

    // Calculate available funds including overdraft
    double currentLimit = getOverdraftLimit();
    double availableFunds = getBalance() + currentLimit;

    if (amount <= availableFunds) {
      setBalance(getBalance() - amount);
      TerminalUI.printCentered("Withdraw Successful: RM " + amount);

      if (getBalance() < 0) {
        TerminalUI.printCentered(String.format("Warning: Using Overdraft: RM %.2f", Math.abs(getBalance())));
      }
      return true;
    } else {
      TerminalUI.printCentered("Withdraw Failed: Exceeds overdraft limit.");
      TerminalUI.printCentered(String.format("Maximum amount available to withdraw is: RM %.2f", availableFunds));
      return false;
    }
  }
}
