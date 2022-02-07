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

    /**
     * Power abstraction
     * @param input Value to power
     * @param power Power
     * @return input^power
     */
    public static double power(double input, int power){
        if(power == 0){
            return 1;
        }
        double output = input;
        for(int i = 1; i < power; i++){
            output *= input;
        }
        return output;
    }
}
