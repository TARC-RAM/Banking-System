import java.util.Scanner;

public class MainMenu {
  static Scanner scnr = new Scanner(System.in);

  public static int dashboard() {
    System.out.println("\n╔════════════════════════════════════════════╗");
    System.out.println("║            TAR DIGITAL BANKING             ║");
    System.out.println("╠════════════════════════════════════════════╣");
    System.out.println("║  [1] Create an account                     ║");
    System.out.println("║  [2] Login                                 ║");
    System.out.println("║  [0] Exit                                  ║");
    System.out.println("╚════════════════════════════════════════════╝");

    while (true) {
      System.out.print("Please enter your option: ");
      String input = scnr.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number.");
      }
    }
  }

  public static void main(String[] args) {
    while (true) {
      int action = MainMenu.dashboard();

      if (action == 1) {
        Register reg = new Register();
        reg.collectInput();
      } else if (action == 2) {
        Login log = new Login();
        log.loginLoop();
      } else if (action == 0) {
        System.out.println("Goodbye!");
        break;
      } else {
        System.out.println("Invalid option. Please choose 1, 2, or 0.");
      }
    }
  }
}
