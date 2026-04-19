import java.util.ArrayList;
import java.util.UUID;

public class TransactionManager {
    private ArrayList<Transaction> transactionHistory = new ArrayList<>();

    // Logic for Deposit
    public void executeDeposit(Account account, double amount) {
        if (amount <= 0) {
            TerminalUI.printCentered("Invalid amount. Deposit must be greater than 0.");
            return;
        }
        
        account.deposit(amount);
        
        Transaction t = new Transaction("Deposit", amount, "DEP-" + UUID.randomUUID().toString().substring(0, 8),
                account.getUserUUID(), account.getAccountNumber());
        transactionHistory.add(t);
    }

    // Logic for Withdraw
    public void executeWithdraw(Account account, double amount) {
        boolean success = account.withdraw(amount);
        
        if (success) {
            Transaction t = new Transaction("Withdraw", amount, "WDL-" + UUID.randomUUID().toString().substring(0, 8),
                    account.getUserUUID(), account.getAccountNumber());
            transactionHistory.add(t);
        }
    }

    // Logic for Transfer between two accounts
    public void executeTransfer(Account source, Account destination, double amount) {
        if (source.getIsFrozen()) {
            TerminalUI.printCentered("Transfer Failed: Source account is frozen.");
            return;
        }

        if (source.withdraw(amount)) {
            destination.deposit(amount);
            
            String ref = "TRF-" + UUID.randomUUID().toString().substring(0, 8);
            Transaction t = new Transaction("Transfer to " + destination.getAccountNumber(), amount, ref,
                    source.getUserUUID(), source.getAccountNumber());
            transactionHistory.add(t);
            TerminalUI.printCentered("Transfer of RM " + amount + " to " + destination.getAccountNumber() + " successful.");
        } else {
            TerminalUI.printCentered("Transfer Failed: Source account has insufficient funds.");
        }
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void printHistory() {
        TerminalUI.printCentered("╔════════════════════════════════════════════╗");
        TerminalUI.printCentered("║            TRANSACTION HISTORY             ║");
        TerminalUI.printCentered("╚════════════════════════════════════════════╝");
        for (Transaction t : transactionHistory) {
            TerminalUI.printCentered(t.toString());
        }
    }
}
