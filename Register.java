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
import java.util.UUID; //Generate a UUID for the user

public class Register {
  private String username;
  private String password;
  ArrayList<UserCredentials> users = new ArrayList<>();
  Scanner scanner;
  FileManager fm = new FileManager();

  public Register() {
    this(new Scanner(System.in));
  }

  public Register(Scanner scanner) {
    this.scanner = scanner;
  }

  public void collectInput() {
    UserCredentials cred = new UserCredentials();
    RegisterValidation rv = new RegisterValidation();
    users = fm.loaduser();

    // Loop until valid username
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your username: "));
      username = scanner.nextLine();
      if (rv.hasSpecialChars(username) == false || rv.hasWhiteSpace(username) == false) {
        TerminalUI.printCentered("Username must be 'A-Z' and '0-9' only.");
      } else if (rv.meetsMinLength(username) == false) {
        TerminalUI.printCentered("Username must be more than 8 characters.");
      } else if (isDuplicateUsername(username) == true) {
        TerminalUI.printCentered("User already exists.");
      } else {
        cred.setUsername(username);
        break;
      }
    }

    // Loop until valid password
    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your password: "));
      password = scanner.nextLine();
      if (rv.hasSpecialChars(password) == false || rv.hasWhiteSpace(password) == false) {
        TerminalUI.printCentered("Password must be 'A-Z' and '0-9' only.");
      } else if (rv.meetsMinLength(password) == false) {
        TerminalUI.printCentered("Password must be more than 8 characters.");
      } else {
        cred.setPassword(password);
        break;
      }
    }

    // At this point both username and password are valid
    String uuid = UUID.randomUUID().toString();
    cred.setUUID(uuid);
    users.add(cred);
    fm.saveUsers(users);
    TerminalUI.printCentered("Registration successful!");
  }

  public boolean isDuplicateUsername(String input) {
    for (UserCredentials user : users) {
      if (input.equals(user.getUsername()))
        return true;
    }
    return false;
  }

  public static void main(String[] args) {
    Register reg = new Register(); // create an object to get the pass & user
    reg.collectInput();
  }
}
