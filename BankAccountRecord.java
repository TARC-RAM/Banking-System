public class BankAccountRecord {
  private String userUUID;
  private String username;
  private String linkedProfileAccountNumber;
  private String accountNumber;
  private String accountType;
  private double balance;
  private double interestRate;
  private double overdraftLimit;
  private boolean frozen;

  public String getUserUUID() {
    return userUUID;
  }

  public void setUserUUID(String userUUID) {
    this.userUUID = userUUID;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLinkedProfileAccountNumber() {
    return linkedProfileAccountNumber;
  }

  public void setLinkedProfileAccountNumber(String linkedProfileAccountNumber) {
    this.linkedProfileAccountNumber = linkedProfileAccountNumber;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAccountType() {
    return accountType;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public double getInterestRate() {
    return interestRate;
  }

  public void setInterestRate(double interestRate) {
    this.interestRate = interestRate;
  }

  public double getOverdraftLimit() {
    return overdraftLimit;
  }

  public void setOverdraftLimit(double overdraftLimit) {
    this.overdraftLimit = overdraftLimit;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }
}
