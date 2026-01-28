package utils;

public class StringUtils {

    // Reverses a string
    public static String reverse(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }

    // Checks if a string is a palindrome
    public static boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String cleaned = input.replaceAll("\\s+", "").toLowerCase();
        return cleaned.equals(reverse(cleaned));
    }
}
