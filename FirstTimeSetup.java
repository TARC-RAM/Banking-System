import java.util.ArrayList;
import java.util.Scanner;

public class FirstTimeSetup {
  Scanner scanner = new Scanner(System.in);
  FileManager fm = new FileManager();
  boolean flag;

  public void setup(UserCredentials user) {
    ArrayList<AccountDetails> accounts = fm.loadAccounts();
    flag = false;
    for (AccountDetails account : accounts) {
      if (user.getUsername().equals(account.getUsername()))
        flag = true;
    }

    if (flag == true) {
      // account screen
    } else {
      AccountDetails newAccount = new AccountDetails();

      String name = "";
      while (true) {
        System.out.print("Enter your Name: ");
        name = scanner.nextLine();
        if (name.matches("[A-Za-z ]+")) {
          break;
        }
        System.out.println("Name can only contain letters and spaces (no @ or special characters).");
      }
      newAccount.setName(name);

      int age = 0;
      boolean validAge = false;
      while (!validAge) {
        System.out.print("Enter your Age: ");
        try {
          age = Integer.parseInt(scanner.nextLine());
          if (age > 0) {
            validAge = true;
          } else {
            System.out.println("Age must be a positive number.");
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter digits only for age (no @ or special characters).");
        }
      }
      newAccount.setAge(age);

      String dob = "";
      while (true) {
        System.out.print("Enter your Date of Birth (DD/MM/YYYY): ");
        dob = scanner.nextLine();
        if (dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
          break;
        }
        System.out.println("DOB must be in DD/MM/YYYY format using digits only.");
      }
      newAccount.setDOB(dob);

      String mykad = "";
      while (true) {
        System.out.print("Enter your MyKad: ");
        mykad = scanner.nextLine();
        if (mykad.matches("\\d+")) {
          break;
        }
        System.out.println("MyKad must be digits only (no @ or special characters).");
      }
      newAccount.setMykad(mykad);

      char gender = 'U';
      while (true) {
        System.out.print("Select your Gender (1 = Male, 2 = Female): ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
          gender = 'M';
          break;
        } else if (choice.equals("2")) {
          gender = 'F';
          break;
        }
        System.out.println("Please enter 1 for Male or 2 for Female.");
      }
      newAccount.setGender(gender);

      newAccount.setUsername(user.getUsername());
      newAccount.setUUID(user.getUUID());
      accounts.add(newAccount);
      fm.saveAccounts(accounts);
      System.out.println("Account details saved.");
    }
  }

}
