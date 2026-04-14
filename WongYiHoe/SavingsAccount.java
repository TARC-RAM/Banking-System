public class SavingsAccount extends Account {
    private double interestRate;
    
    public SavingsAccount(String customerID, double initialBalance, double interestRate) {
        super(customerID, initialBalance);
        this.interestRate = interestRate;
    }

    public SavingsAccount(String customerID, String accountNumber, double initialBalance, double interestRate) {
        super(customerID, accountNumber, initialBalance);
        this.interestRate = interestRate;
    }

    // Getters and Setters
    public double getInterestRate() { 
        return interestRate; }

    public void setInterestRate(double interestRate) { 
        this.interestRate = interestRate; }

    @Override
    public void performMonthlyProcess() {

        if (getIsFrozen()) {
            System.out.println("Monthly Process Failed: Account is frozen.");
            return;
        }
        else {
            double interest = getBalance() * (interestRate / 100);
            setBalance(getBalance() + interest);
            System.out.println("Monthly Process: Interest added RM " + interest);
        }
    }
}
