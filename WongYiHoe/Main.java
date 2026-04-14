import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Initialize account variable for abstract class reference
        Account myAccount = null; 
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║             RAM RAM DIGITAL BANK           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n---PLEASE SELECT ACCOUNT TYPE---");
        System.out.println("    [1]     Savings Account");
        System.out.println("    [2]     Current Account\n");
        System.out.print("Please enter (1 / 2): ");
        
        int accountType = scanner.nextInt();
        
        // --- account initialization ---
        if (accountType == 1) {
            // Create Savings Account
            myAccount = new SavingsAccount("CUST001", 1000.0, 5.0);
            System.out.println("\n>>> Savings Account created successfully!");
            System.out.println("      Account Number    : " + myAccount.getAccountNumber());
            System.out.println("      Initial Balance   : RM 1000.0");
            System.out.println("      Interest Rate     : 5%");
        } else if (accountType == 2) {
            // Create Current Account
            myAccount = new CurrentAccount("CUST001", 5000.0);
            System.out.println("\n>>> Current Account created successfully!");
            System.out.println("      Account Number    : " + myAccount.getAccountNumber());
            System.out.println("      Initial Balance   : " + "RM 5000.0");
            System.out.println("      Overdraft Limit   : " + "RM 2500.0");
        } else {
            System.out.println("Invalid selection, exiting the system...");
            scanner.close();
            return;
        }

        // Menu loop for user operations
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("\n\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                      Ram Ram Digital Bank                 ║");
            System.out.println("║                                                          ║");
            System.out.println("║  [1] DEPOSIT FUNDS                                       ║");
            System.out.println("║  [2] WITHDRAW FUNDS                                      ║");
            System.out.println("║  [3] CHECK BALANCE                                       ║");
            System.out.println("║                                                          ║");
            System.out.println("║  [0] EXIT                                                ║");
            System.out.println("║                                                          ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.print("PLEASE ENTER: ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:// use the deposit method
                    System.out.print("\nPlease enter the deposit amount\t: RM ");
                    double depAmount = scanner.nextDouble();
                    myAccount.deposit(depAmount);
                    System.out.println("\nCurrent Balance\t: RM " + myAccount.getBalance());
                    break;
                    
                case 2:// use the withdraw method (overdraft)
                    if (myAccount instanceof CurrentAccount) {
                        CurrentAccount ca = (CurrentAccount) myAccount;
                        System.out.println("\nAvailable Funds\t: RM " + (ca.getBalance() + ca.getOverdraftLimit()));
                    } else {
                        System.out.println("\nAvailable Funds\t: RM " + myAccount.getBalance());// For Savings Account, available funds is just the balance
                    }
                    System.out.print("\nPlease enter the withdrawal amount: RM ");
                    double withAmount = scanner.nextDouble();
                    System.out.println();
                    myAccount.withdraw(withAmount);
                    System.out.println("\nCurrent Balance\t: RM " + myAccount.getBalance());
                    break;

                case 3: // Check Balance
                    System.out.println("\n  --------------------------------------");
                    System.out.printf("  Current Balance\t: RM %.2f\n", myAccount.getBalance());
    
                    if (myAccount instanceof CurrentAccount) {

                        CurrentAccount ca = (CurrentAccount) myAccount;
                        double available = ca.getBalance() + ca.getOverdraftLimit();
        
                        System.out.printf("  Overdraft Limit\t: RM %.2f\n", ca.getOverdraftLimit());
                        System.out.printf("  Total Available\t: RM %.2f\n", available);
                    }
                    System.out.println("  --------------------------------------");
                    break;
                    
                case 0:
                    isRunning = false;
                    System.out.println("\n>>> Thank you for using Digital Bank. Goodbye!");
                    break;
                    
                default:
                    System.out.println("\n[Error] Invalid input, please try again!");
                    break;
            }
        }
        
        scanner.close();
    }
}