import java.util.Scanner;

public class MainMenu {
  static Scanner scnr = new Scanner(System.in);

  public static int dashboard() {
    int action = 0;

    System.out.println("Welcome to TAR Digital Banking!");
    System.out.println("Enter Option number to decide an action!");
    System.out.println("Option (1) Create an account");
    System.out.println("Option (2) Login");
    System.out.printf("Enter your Option: ");
    action = scnr.nextInt();
    return action;

  }

  public static void main(String[] args) {
    int action = MainMenu.dashboard();

    if (action == 1) {
      Register reg = new Register();
      reg.collectInput();
    }
  }
}
