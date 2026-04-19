import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.FileWriter; //Write the json file
import java.io.FileReader; //Read the Json File
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class FileManager {
  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public ArrayList<UserCredentials> loaduser() {
    try (FileReader reader = new FileReader("users.json")) {
      Type type = new TypeToken<ArrayList<UserCredentials>>() {
      }.getType();
      ArrayList<UserCredentials> users = gson.fromJson(reader, type);
      return users == null ? new ArrayList<>() : users;
    } catch (JsonSyntaxException e) {
      TerminalUI.printCentered("users.json is malformed. Please fix the file.");
    } catch (IOException e) {
      TerminalUI.printCentered("An error has occurred.");
    }
    return new ArrayList<>();
  }

  public void saveUsers(ArrayList<UserCredentials> users) {
    String credJson = gson.toJson(users);
    try (FileWriter writer = new FileWriter("users.json")) {
      writer.write(credJson);
      TerminalUI.printCentered("User registered successfully");
    } catch (IOException e) {
      TerminalUI.printCentered("An error occurred while writing to users.json: " + e.getMessage());
    }
  }

  public ArrayList<AccountDetails> loadAccounts() {
    try (FileReader reader = new FileReader("accounts.json")) {
      Type type = new TypeToken<ArrayList<AccountDetails>>() {
      }.getType();
      ArrayList<AccountDetails> accounts = gson.fromJson(reader, type);
      return accounts == null ? new ArrayList<>() : accounts;
    } catch (JsonSyntaxException e) {
      TerminalUI.printCentered("accounts.json is malformed. Please fix the file.");
    } catch (IOException e) {
      TerminalUI.printCentered("An error has occurred.");
    }
    return new ArrayList<>();
  }

  public void saveAccounts(ArrayList<AccountDetails> users) {
    String credJson = gson.toJson(users);
    try (FileWriter writer = new FileWriter("accounts.json")) {
      writer.write(credJson);
      TerminalUI.printCentered("Account registered successfully");
    } catch (IOException e) {
      TerminalUI.printCentered("An error occurred while writing to accounts.json: " + e.getMessage());
    }
  }

  public ArrayList<BankAccountRecord> loadBankAccounts() {
    try (FileReader reader = new FileReader("bank_accounts.json")) {
      Type type = new TypeToken<ArrayList<BankAccountRecord>>() {
      }.getType();
      ArrayList<BankAccountRecord> records = gson.fromJson(reader, type);
      return records == null ? new ArrayList<>() : records;
    } catch (JsonSyntaxException e) {
      TerminalUI.printCentered("bank_accounts.json is malformed. Please fix the file.");
      return new ArrayList<>();
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public void saveBankAccounts(ArrayList<BankAccountRecord> accounts) {
    String accountJson = gson.toJson(accounts);
    try (FileWriter writer = new FileWriter("bank_accounts.json")) {
      writer.write(accountJson);
      TerminalUI.printCentered("Bank account data saved successfully");
    } catch (IOException e) {
      TerminalUI.printCentered("An error occurred while writing to bank_accounts.json: " + e.getMessage());
    }
  }

  // Transaction persistence - stores transaction history
  public ArrayList<Transaction> loadTransactions() {
    try (FileReader reader = new FileReader("transactions.json")) {
      Type type = new TypeToken<ArrayList<Transaction>>() {
      }.getType();
      ArrayList<Transaction> transactions = gson.fromJson(reader, type);
      return transactions == null ? new ArrayList<>() : transactions;
    } catch (JsonSyntaxException e) {
      TerminalUI.printCentered("transactions.json is malformed. Please fix the file.");
      return new ArrayList<>();
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public void saveTransactions(ArrayList<Transaction> transactions) {
    String transactionJson = gson.toJson(transactions);
    try (FileWriter writer = new FileWriter("transactions.json")) {
      writer.write(transactionJson);
      TerminalUI.printCentered("Transaction data saved successfully");
    } catch (IOException e) {
      TerminalUI.printCentered("An error occurred while writing to transactions.json: " + e.getMessage());
    }
  }

  // Customer metadata persistence
  public ArrayList<Customer> loadCustomers() {
    try (FileReader reader = new FileReader("customers.json")) {
      Type type = new TypeToken<ArrayList<Customer>>() {
      }.getType();
      ArrayList<Customer> customers = gson.fromJson(reader, type);
      return customers == null ? new ArrayList<>() : customers;
    } catch (JsonSyntaxException e) {
      TerminalUI.printCentered("customers.json is malformed. Please fix the file.");
      return new ArrayList<>();
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public void saveCustomers(ArrayList<Customer> customers) {
    String customerJson = gson.toJson(customers);
    try (FileWriter writer = new FileWriter("customers.json")) {
      writer.write(customerJson);
      TerminalUI.printCentered("Customer data saved successfully");
    } catch (IOException e) {
      TerminalUI.printCentered("An error occurred while writing to customers.json: " + e.getMessage());
    }
  }
}
