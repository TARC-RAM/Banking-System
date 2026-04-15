import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String timestamp;
    private String type; // "Deposit", "Withdraw", or "Transfer"
    private double amount;
    private String referenceID;
    private String userUUID;
    private String accountNumber;

    public Transaction(String type, double amount, String referenceID, String userUUID, String accountNumber) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.type = type;
        this.amount = amount;
        this.referenceID = referenceID;
        this.userUUID = userUUID;
        this.accountNumber = accountNumber;
    }

    // Getters
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getReferenceID() { return referenceID; }
    public String getUserUUID() { return userUUID; }
    public String getAccountNumber() { return accountNumber; }

    @Override
    public String toString() {
        return String.format("[%s] %s: RM %.2f (Ref: %s)", timestamp, type, amount, referenceID);
    }
}
