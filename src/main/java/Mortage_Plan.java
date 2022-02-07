import javax.swing.*;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import utilities.Utilities;
import  utilities.Utilities.*;
import utilities.*;

public class Mortage_Plan {

    static String decimalDenominator = ".";

    public static void main(String[] args){

        String inputFileDir;

        if(args.length == 0){
            System.out.println("Please enter file directory: ");
            Console c = System.console();
            if(c == null){
                System.out.println("No console found. Please enter directory as argument...");
                System.exit(1);
            }
            inputFileDir = c.readLine();
        }
        else{
            inputFileDir = args[0];
        }

        Prospect[] prospects;
        try{
            prospects = getProspects(inputFileDir);
        }
        catch ( FileNotFoundException error){
            System.out.printf("File \"%s\" not found. Exiting...%n", inputFileDir);
            System.exit(0);
            prospects = new Prospect[0];//Stops warning. Never able to run
        }

        int index = 0;
        for(Prospect p : prospects){
            DisplayProspect(p, ++index);
        }
    }

    static void DisplayProspect(Prospect p, int index){
        int[] MonthlyPayment = CalculateMonthlyPayment(p);
        System.out.printf("Prospect %d: %s wants to borrow %d.%d for a period of %d years and pay %d.%d € each month\n",
                index, p.Name, p.TotalLoan, p.TotalLoanDecimal, p.Years, MonthlyPayment[0], MonthlyPayment[1]);
    }

    /**
     * Calculates monthly payment for prospect
     * @param p Prospect to calculate
     * @return int[2] with monthly payment
     */
    //E = U(b(1 + b)^p) / ((1 + b)^p - 1)
    //E = Monthly Payment
    //U = Loan amount
    //b = Interest percent
    //p = payments (i.e years * 12)
    static int[] CalculateMonthlyPayment(Prospect p){
        //Abstraction
        int[] interest = {p.Interest, p.InterestDecimal};
        int[] totalLoan = {p.TotalLoan, p.TotalLoanDecimal};
        int payments = p.Years * 12;

        //Use floats since we are dealing with division

        //Calculate b
        float interestPercent = (float)interest[0] / 100 + ((float)interest[1] / 100) / 100;

        //Calculate U(b(1 + b)^p). Lets call this X. Now E = X / ((1 + b)^p - 1)
        float totalInterestCost = (float) totalLoan[0] * (interestPercent * power((interestPercent + 1), payments));
        float totalInterestCostDecimal = (float) totalLoan[1] * (interestPercent * power((interestPercent + 1), payments));

        //Shave of remaining decimals to separate var
        totalInterestCostDecimal += totalInterestCost % 1 * 100;
        totalInterestCost -= totalInterestCost % 1;

        //E = X / ((1 + b)^p - 1)
        float MonthlyPayment = totalInterestCost / (float)(power(1 + interestPercent, payments) - 1);
        float MonthlyPaymentDecimal = totalInterestCostDecimal / (float)(power(1 + interestPercent, payments) - 1);

        //Add decimals to Decimal variable
        MonthlyPaymentDecimal += MonthlyPayment % 1 * 100;
        MonthlyPayment -= MonthlyPayment % 1; //Remove decimals

        //Add back excess decimals to main var (123.456 -> 127.056)
        while(MonthlyPaymentDecimal > 100){
            MonthlyPayment++;
            MonthlyPaymentDecimal -= 100;
        }

        //Round decimal
        if(MonthlyPaymentDecimal % 1 > 0.5){
            MonthlyPaymentDecimal++;
        }
        //Now remove excess
        MonthlyPaymentDecimal -= MonthlyPaymentDecimal % 1;

        //Back to ints now that everything is rounded
        return new int[] {(int)MonthlyPayment, (int)MonthlyPaymentDecimal};
    }

    /**
     * Power abstraction
     * @param input Value to power
     * @param power Power
     * @return input^power
     */
    static float power(float input, int power){
        if(power == 0){
            return 1;
        }
        float output = input;
        for(int i = 1; i < power; i++){
            output *= input;
        }
        return output;
    }

    /**
     * Read prospects from file
     * @param fileName Directory of file to read
     * @return List of prospects read from file
     * @throws FileNotFoundException If not file found
     */
    static Prospect[] getProspects(String fileName) throws FileNotFoundException {
        List<Prospect> prospectList = new ArrayList<>();

        File inputFile = new File(fileName);
        Scanner reader = new Scanner(inputFile);

        reader.nextLine();//Skip first line, it contains column titles

        while(reader.hasNextLine()){
            Prospect newProspect = attemptReadProspect(reader.nextLine());
            if(newProspect.Valid) {
                prospectList.add(newProspect);
            }
        }

        return prospectList.toArray(Prospect[]::new);
    }

    /**
     * Reads prospect from input string
     * @param line String to read prospect from
     * @return Prospect read from line
     */
    static Prospect attemptReadProspect(String line) {

        //If line contains '"', it means the name of prospect might contain a comma.
        List<Integer> commasInName = getCommas(line);
        //Temporarily remove commas in name
        for (Integer pos : commasInName) {
            line = Utilities.removeAtPosition(line, pos);
        }

        String[] elements = line.split(",");
        if (elements.length == 4) {

            int[] totalLoan = attemptReadDecimal(elements[1]);
            int[] interest = attemptReadDecimal(elements[2]);
            int years = Integer.parseInt(elements[3]);

            return new Prospect(reFormatName(elements[0], commasInName) //Name
                    , totalLoan[0], totalLoan[1]                        //Total loan
                    , interest[0], interest[1]                          //Interest
                    , years                                             //Years
                    , Boolean.TRUE);                                    //Valid
        }
        else{
            return new Prospect("Invalid", 0, 0, 0,0, 0,false);
        }
        //TODO more checks
    }

    /**
     * Get list of all commas in string in quotations
     * @param line String to read
     * @return List of commas
     */
    static List<Integer> getCommas(String line){
        List<Integer> commasInName = new ArrayList<>();

        //check if line is empty
        if(line.length() == 0){
            return commasInName;
        }

        if(line.charAt(0) == '\"'){
            //Loop until next '"'
            int i = 1;
            while(line.charAt(i) != '\"' && i <= line.length()){
                i++;
                //Check if current char is comma
                if(line.charAt(i) == ','){
                    //If so add to list
                    commasInName.add(i);
                }
            }
        }

        return commasInName;
    }

    /**
     * Reimplement commas
     * @param element String to add commas to
     * @param commas List of where to add commas
     * @return Formatted string
     */
    static String reFormatName(String element, List<Integer> commas){
        String name = element;
        for (Integer commaPoint : commas) {
            name = name.substring(0, commaPoint) + ',' + name.substring(commaPoint);
        }
        return name;
    }



    /**
     * Read decimal and return as two ints. If no decimal second int will be 0
     * @param input Value to read
     * @return int[2] with both whole number and decimal (if no decimal is provided it will be 0)
     */
    static int[] attemptReadDecimal(String input){
        String[] elements;
        if(input.contains(decimalDenominator)) {
            elements = input.split("\\" + decimalDenominator);
        }
        else{
            elements = new String[1];
            elements[0] = input;
        }
        int[] toReturn = new int[2];
        toReturn[0] = Integer.parseInt(elements[0]);
        if(elements.length == 2){
            toReturn[1] = Integer.parseInt(elements[1]);
        }
        //Note toReturn[1] is already set to 0
        return toReturn;
    }
}
class Prospect{
    String Name;
    int TotalLoan;
    int TotalLoanDecimal;//Floating points are not precise enough in this context.
    int Interest;
    int InterestDecimal;
    int Years;
    boolean Valid; //Check if this prospect read correctly and is valid to use

    public Prospect(String name, int totalLoan, int totalLoanDecimal, int interest, int interestDecimal, int years, boolean valid){
        Name = name;
        TotalLoan = totalLoan;
        TotalLoanDecimal = totalLoanDecimal;
        Interest = interest;
        InterestDecimal = interestDecimal;
        Years = years;
        Valid = valid;
    }

    //https://www.javaprogramto.com/2020/08/how-to-compare-two-objects-in-java-8.html
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Prospect p = (Prospect)o;

        return p.Valid == this.Valid
                && p.Interest == this.Interest
                && p.InterestDecimal == this.InterestDecimal
                && p.Name.compareTo(this.Name) == 0 //https://www.w3schools.com/java/ref_string_compareto.asp
                && p.TotalLoan == this.TotalLoan
                && p.TotalLoanDecimal == this.TotalLoanDecimal
                && p.Years == this.Years;
    }
}