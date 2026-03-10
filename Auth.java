import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Auth {
  static Scanner scnr = new Scanner(System.in);
  private static final String USERS_FILE = "users.json";
  private static final Gson gson = new Gson();

  public static void register() {
    System.out.println("=== Register ===");
    System.out.print("Enter username: ");
    String username = scnr.nextLine();

    System.out.print("Enter password: ");
    String password = scnr.nextLine();

    SavedUser savedUser = new SavedUser(username, password);
    saveUsers(savedUser);
  }

  private static void saveUsers(SavedUser savedUser) {
    try (FileWriter writer = new FileWriter(USERS_FILE)) {
      gson.toJson(savedUser, writer);
    } catch (IOException e) {
      System.out.println("Failed to save user data.");
    }

  }

  static class SavedUser {
    String username;
    String password;

    SavedUser(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }

}
