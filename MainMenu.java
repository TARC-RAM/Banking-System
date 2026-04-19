import java.util.Scanner;

public class MainMenu {
  static Scanner scnr = new Scanner(System.in);

  public static int dashboard() {
    TerminalUI.clearScreen();
    TerminalUI.printCentered("╔════════════════════════════════════════════╗");
    TerminalUI.printCentered("║            TAR DIGITAL BANKING             ║");
    TerminalUI.printCentered("╠════════════════════════════════════════════╣");
    TerminalUI.printCentered("║  [1] Create an account                     ║");
    TerminalUI.printCentered("║  [2] Login                                 ║");
    TerminalUI.printCentered("║  [0] Exit                                  ║");
    TerminalUI.printCentered("╚════════════════════════════════════════════╝");

    while (true) {
      System.out.print(TerminalUI.menuPrompt("Please enter your option: "));
      String input = scnr.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        TerminalUI.printCentered("Invalid input. Please enter a number.");
      }
    }
  }

  public static void main(String[] args) {
    while (true) {
      int action = MainMenu.dashboard();

      if (action == 1) {
        Register reg = new Register(scnr);
        reg.collectInput();
      } else if (action == 2) {
        Login log = new Login(scnr);
        log.loginLoop();
      } else if (action == 0) {
        TerminalUI.printCentered("Goodbye!");
        break;
      } else {
        TerminalUI.printCentered("Invalid option. Please choose 1, 2, or 0.");
        TerminalUI.pauseForEnter(scnr);
      }
    }
  }
}
