package utilities;
public class Utilities {
    /**
     * Abstraction of StringBuilder.deleteCharAt
     * @param input Input string
     * @param pos Index of char to remove
     * @return Formatted string
     */

    public static String removeAtPosition(String input, Integer pos) {
        StringBuilder sb = new StringBuilder(input);
        sb.deleteCharAt(pos);
        return sb.toString();
    }
}
