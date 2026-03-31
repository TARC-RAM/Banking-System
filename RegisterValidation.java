public class RegisterValidation {

  public boolean hasSpecialChars(String input) {
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);

      if ((Character.isLetter(ch) == false && Character.isDigit(ch) == false))
        return false;
    }
    return true;
  }

  public boolean hasWhiteSpace(String input) {
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);

      if ((Character.isWhitespace(ch) == true))
        return false;
    }
    return true;
  }

  public boolean meetsMinLength(String input) {
    if (input.length() < 8)
      return false;
    return true;
  }

}
