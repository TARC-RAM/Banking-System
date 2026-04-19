public class SavingsAccount extends Account {
    private double interestRate;
    
    public SavingsAccount(String userUUID, String customerID, double initialBalance, double interestRate) {
        super(userUUID, customerID, initialBalance, "SA");
        this.interestRate = interestRate;
    }

    public SavingsAccount(String userUUID, String customerID, String accountNumber, double initialBalance, double interestRate) {
        super(userUUID, customerID, accountNumber, initialBalance);
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
            TerminalUI.printCentered("Monthly Process Failed: Account is frozen.");
            return;
        }
        else {
            double interest = getBalance() * (interestRate / 100);
            setBalance(getBalance() + interest);
            TerminalUI.printCentered("Monthly Process: Interest added RM " + interest);
        }
    }
}
