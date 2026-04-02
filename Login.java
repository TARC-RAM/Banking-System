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

public class Login {
  private String username;
  private String password;
  FileManager fm = new FileManager();
  Scanner scanner = new Scanner(System.in);
  ArrayList<UserCredentials> users = new ArrayList<>();

  public void getLogin() {
    System.out.print("Enter your username: ");
    username = scanner.nextLine();
    System.out.print("Enter your password: ");
    password = scanner.nextLine();
  }

  public void confirmLogin() {
    users = fm.loaduser();
    boolean found = false;
    for (UserCredentials user : users) {
      if (username.equals(user.getUsername()) && (password.equals(user.getPassword()))) {
        found = true;
        System.out.println("Login Successfull!");
        break; // stop the loop once a match is found
      }
    }
    if (found == false) {
      System.out.println("Username or Password is wrong");
    }
  }
}
