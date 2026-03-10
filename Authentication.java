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

public class Authentication {
  private static final Scanner scnr = new Scanner(System.in);
  private static final String USERS_FILE = "users.json";
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() {
  }.getType();

  public static void register() {
    System.out.println("=== Register ===");
    System.out.print("Enter username: ");
    String username = scnr.nextLine().trim();

    System.out.print("Enter password: ");
    String password = scnr.nextLine().trim();

    List<User> users = loadUsers();

    for (User user : users) {
      if (user.username.equals(username)) {
        System.out.println("Username already exists.");
        return;
      }
    }

    users.add(new User(username, password));
    saveUsers(users);
    System.out.println("Account registered successfully.");
  }

  public static boolean login() {
    System.out.println("=== Login ===");
    System.out.print("Enter username: ");
    String username = scnr.nextLine().trim();

    System.out.print("Enter password: ");
    String password = scnr.nextLine().trim();

    List<User> users = loadUsers();

    for (User user : users) {
      if (user.username.equals(username) && user.password.equals(password)) {
        System.out.println("Login successful.");
        return true;
      }
    }

    System.out.println("Invalid username or password.");
    return false;
  }

  private static List<User> loadUsers() {
    try (Reader reader = new FileReader(USERS_FILE)) {
      List<User> users = gson.fromJson(reader, USER_LIST_TYPE);
      if (users == null) {
        return new ArrayList<>();
      }
      return users;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  private static void saveUsers(List<User> users) {
    try (FileWriter writer = new FileWriter(USERS_FILE)) {
      gson.toJson(users, writer);
    } catch (IOException e) {
      System.out.println("Failed to save user data.");
    }
  }

  private static class User {
    private String username;
    private String password;

    private User(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }
}
