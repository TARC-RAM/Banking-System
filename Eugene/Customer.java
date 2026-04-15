import java.util.ArrayList;
import java.util.List;

public class Customer {
  private String customerId;
  private String userUUID;
  private String name;
  private String email;
  private String phoneNumber;
  private List<String> accountNumbers;

  public Customer(String userUUID, String name, String email, String phoneNumber) {
    this.customerId = generateCustomerId();
    this.userUUID = userUUID;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.accountNumbers = new ArrayList<>();
  }

  private String generateCustomerId() {
    return "CUST" + System.currentTimeMillis() % 1000000;
  }

  public void addAccountNumber(String accountNumber) {
    this.accountNumbers.add(accountNumber);
  }

  // Getters and Setters for Encapsulation
  public String getCustomerId() {
    return customerId;
  }

  public String getUserUUID() {
    return userUUID;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public List<String> getAccountNumbers() {
    return accountNumbers;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
