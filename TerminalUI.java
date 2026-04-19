import java.util.Scanner;

public final class TerminalUI {
  private static final int DISPLAY_WIDTH = 68;
  private static final int RIGHT_SHIFT = 50;
  private static final int TOP_PADDING_LINES = 2;
  private static final int MENU_BLOCK_WIDTH = 44;
  private static final int CENTER_PADDING = Math.max(0, (DISPLAY_WIDTH - MENU_BLOCK_WIDTH) / 2) + RIGHT_SHIFT;

  private TerminalUI() {
  }

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
    for (int i = 0; i < TOP_PADDING_LINES; i++) {
      System.out.println();
    }
  }

  public static String center(String text) {
    if (text == null) {
      return "";
    }
    return " ".repeat(CENTER_PADDING) + text;
  }

  public static String menuPrompt(String text) {
    int padding = Math.max(0, (DISPLAY_WIDTH - MENU_BLOCK_WIDTH) / 2) + RIGHT_SHIFT;
    return " ".repeat(padding) + text;
  }

  public static void printCentered(String text) {
    System.out.println(center(text));
  }

  public static void pauseForEnter(Scanner scanner) {
    System.out.print(center("Press Enter to continue..."));
    scanner.nextLine();
  }
}
