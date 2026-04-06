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
  Scanner scanner = new Scanner(System.in);
  FileManager fm = new FileManager();

  public void collectInput() {
    UserCredentials cred = new UserCredentials();
    RegisterValidation rv = new RegisterValidation();
    users = fm.loaduser();
    boolean validinput = true;

    System.out.print("Enter your username: ");
    username = scanner.nextLine();
    if (rv.hasSpecialChars(username) == false || rv.hasWhiteSpace(username) == false) {
      System.out.println("Username must be 'A-Z' and '0-9' only.");
      validinput = false;
    } else if (rv.meetsMinLength(username) == false) {
      System.out.println("Username must also be more than 8 characters");
    } else if (isDuplicateUsername(username) == true) {
      System.out.println("User already exists");
      validinput = false;
    } else {
      cred.setUsername(username); // send to getter
    }

    System.out.print("Enter your passsword: ");
    password = scanner.nextLine();
    if (rv.hasSpecialChars(password) == false || rv.hasWhiteSpace(password) == false) {
      System.out.println("Password must be 'A-Z' and '0-9' only.");
      validinput = false;
    } else if (rv.meetsMinLength(password) == false) {
      System.out.println("Password must also be more than 8 characters");
    } else {
      cred.setPassword(password); // send to getter
    }
    if (validinput) {
      String uuid = UUID.randomUUID().toString(); // use toString to convert the UUID object
                                                  // into a readable string to send to getter
      cred.setUUID(uuid);
      users.add(cred);
      fm.saveUsers(users);
    }
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
