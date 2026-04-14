import com.google.gson.Gson;
import java.io.FileWriter; //Write the json file
import java.io.FileReader; //Read the Json File
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors

public class FileManager {
  Gson gson = new Gson();

  public ArrayList<UserCredentials> loaduser() {
    try (FileReader reader = new FileReader("users.json")) {
      Type type = new TypeToken<ArrayList<UserCredentials>>() {
      }.getType();
      return gson.fromJson(reader, type);
    } catch (IOException e) {
      System.out.println("An error has occured");
    }
    return new ArrayList<>();
  }

  public void saveUsers(ArrayList<UserCredentials> users) {
    String credJson = gson.toJson(users);
    try (FileWriter writer = new FileWriter("users.json")) {
      writer.write(credJson);
      System.out.println("User registered successfully");
    } catch (IOException e) {
      System.out.println("An error occured while writing to the file: " + e.getMessage());
    }
  }

  public ArrayList<AccountDetails> loadAccounts() {
    try (FileReader reader = new FileReader("accounts.json")) {
      Type type = new TypeToken<ArrayList<AccountDetails>>() {
      }.getType();
      return gson.fromJson(reader, type);
    } catch (IOException e) {
      System.out.println("An error has occured");
    }
    return new ArrayList<>();
  }

  public void saveAccounts(ArrayList<AccountDetails> users) {
    String credJson = gson.toJson(users);
    try (FileWriter writer = new FileWriter("accounts.json")) {
      writer.write(credJson);
      System.out.println("Account registered successfully");
    } catch (IOException e) {
      System.out.println("An error occured while writing to the file: " + e.getMessage());
    }
  }

  public ArrayList<BankAccountRecord> loadBankAccounts() {
    try (FileReader reader = new FileReader("bank_accounts.json")) {
      Type type = new TypeToken<ArrayList<BankAccountRecord>>() {
      }.getType();
      ArrayList<BankAccountRecord> records = gson.fromJson(reader, type);
      return records == null ? new ArrayList<>() : records;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public void saveBankAccounts(ArrayList<BankAccountRecord> accounts) {
    String accountJson = gson.toJson(accounts);
    try (FileWriter writer = new FileWriter("bank_accounts.json")) {
      writer.write(accountJson);
      System.out.println("Bank account data saved successfully");
    } catch (IOException e) {
      System.out.println("An error occured while writing to the file: " + e.getMessage());
    }
  }
}
