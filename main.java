import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class mainmenu {
  static Scanner scnr = new Scanner(System.in);

  public static int menu() {
    System.out.println("Welcome to TAR Digital Banking!");
    System.out.println("Enter Option number to decide an action!");
    System.out.println("Option (1) Create an account");
    System.out.println("Option (2) Login");
    System.out.printf("Enter your Option: ");
    int option = scnr.nextInt();
    scnr.nextLine();
    return option;
  }

  public static void account() {
    System.out.printf("Enter a username: ");
    String username = scnr.nextLine();
    System.out.printf("Enter a password: ");
    String password = scnr.nextLine();
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
      writer.write(username + " | ");
      writer.write(password + "\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static int login() {
    int auth = 0;
    String user = null;
    String pass = null;
    System.out.printf("Enter a username: ");
    String inUser = scnr.nextLine();
    System.out.printf("Enter a password: ");
    String inPass = scnr.nextLine();
    try {
      Scanner scan = new Scanner(new File("/home/ram/Desktop/Banking-System/output.txt"));
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        String[] parts = line.split("\\|");
        user = parts[0].trim();
        pass = parts[1].trim();
      }
      scan.close();
      if (inUser.equals(user) && inPass.equals(pass)) {
        System.out.println("Login successfull");
        auth = 1;
      } else {
        System.out.println("Wrong password");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return auth;
  }

  public static void main(String[] args) {
    int option = menu();
    int auth = 0;
    if (1 == option) {
      account();
    }
    if (2 == option) {
      auth = login();
    }
    System.out.println(auth);
  }
}
