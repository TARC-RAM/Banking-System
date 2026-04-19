import java.util.Scanner;
import java.util.ArrayList;

public class Login {
  private static final String ADMIN_USERNAME = "TARCADMIN";
  private static final String ADMIN_PASSWORD = "TARCADMIN";
  private String username;
  private String password;
  FileManager fm = new FileManager();
  Scanner scanner;
  ArrayList<UserCredentials> users = new ArrayList<>();
  FirstTimeSetup fts;

  public Login() {
    this(new Scanner(System.in));
  }

  public Login(Scanner scanner) {
    this.scanner = scanner;
    this.fts = new FirstTimeSetup(scanner);
  }

  public void loginLoop() {
    users = fm.loaduser();

    while (true) {
      System.out.print(TerminalUI.menuPrompt("Enter your username: "));
      username = scanner.nextLine().trim();
      System.out.print(TerminalUI.menuPrompt("Enter your password: "));
      password = scanner.nextLine().trim();

      if (isAdminCredential(username, password)) {
        TerminalUI.printCentered("Login successful!");
        TerminalUI.printCentered("Admin mode activated.");
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.displayMenu(scanner);
        return;
      }

      for (UserCredentials user : users) {
        if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
          fts.setup(user);
          return;
        }
      }
      TerminalUI.printCentered("User does not exist.");
    }
  }

  private boolean isAdminCredential(String inputUsername, String inputPassword) {
    return ADMIN_USERNAME.equals(inputUsername) && ADMIN_PASSWORD.equals(inputPassword);
  }
}
