public class Main {
    public static void main(String[] args) {
        TerminalUI.printCentered("System Starting...");
        AdminMenu admin = new AdminMenu();
        admin.displayMenu();
    }
}
