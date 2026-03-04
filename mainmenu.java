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

  public static void main(String[] args) {
    int option = menu();
    int auth = 0;
    if (option == 1) {
      account.login();
    }
  }
}
