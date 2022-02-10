import utilities.Utilities;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mortage_Plan {

    static String decimalDenominator = ".";
    static int roundUpOver = 5; //Round up if decimal is higher than this

    public static void main(String[] args){
        //Get input dir
        String inputFileDir = getInputFileDirectory(args);

        //Get all prospects
        Prospect[] prospects = fetchProspects(inputFileDir);

        //Display all prospects
        loopProspects(prospects);
    }

    /**
     * Reads prospects from given file
     * @param inputFileDir Directory of file to read
     * @return Array of prospects
     */
    static Prospect[] fetchProspects(String inputFileDir){
        Prospect[] prospects;
        try{
            prospects = getProspects(inputFileDir);
        }
        //Catch if file not found
        catch ( FileNotFoundException error){
            System.out.printf("File \"%s\" not found. Exiting...%n", inputFileDir);
            System.exit(0);
            prospects = new Prospect[0];//Stops warning. Never able to run
        };
        return prospects;
    }

    /**
     * Loops and displays all prospects in array
     * @param prospects array of prospects
     */
    static void loopProspects(Prospect[] prospects){
        //Loop through all prospects and display them
        int index = 0;
        for(Prospect p : prospects){
            DisplayProspect(p, ++index);
        }
    }

    /**
     * Tries to fetch inputFileDirectory from args. Else prompt user for directory
     * @param args Program arguments
     * @return File directory
     */
    static String getInputFileDirectory(String[] args){
        String inputFileDir;
        //Check for dir in args
        if(args.length == 0){
            Console c = System.console();
            if(c == null){
                System.out.println("No console found. Please enter directory as argument...");
                System.exit(1);
            }
            //Prompt for dir
            System.out.print("Please enter file directory: ");
            inputFileDir = c.readLine();
        }
        else{
            //If dir found in args, use that
            inputFileDir = args[0];
        }
        return inputFileDir;
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
    static int[] CalculateMonthlyPayment(Prospect p){
        //E = U(b(1 + b)^p) / ((1 + b)^p - 1)
        //E = Monthly Payment
        //U = Loan amount
        //b = Interest percent
        //p = payments (i.e years * 12)

        //Abstraction
        int[] interest = {p.Interest, p.InterestDecimal};
        int[] totalLoan = {p.TotalLoan, p.TotalLoanDecimal};
        int payments = p.Years * 12;

        //Use doubles since we are dealing with division

        //Calculate b
        double interestPercent = (double)interest[0] / 100 + ((double)interest[1] / 100) / 100;

        //Calculate U(b(1 + b)^p). Lets call this X. Now E = X / ((1 + b)^p - 1)
        double totalInterestCost = (double) totalLoan[0] * (interestPercent * Utilities.power((interestPercent + 1), payments));
        double totalInterestCostDecimal = (double) totalLoan[1] * (interestPercent * Utilities.power((interestPercent + 1), payments));

        //Shave of remaining decimals to separate var
        totalInterestCostDecimal += totalInterestCost % 1 * 100;
        totalInterestCost -= totalInterestCost % 1;

        //E = X / ((1 + b)^p - 1)
        double MonthlyPayment = totalInterestCost / (Utilities.power(1 + interestPercent, payments) - 1);
        double MonthlyPaymentDecimal = totalInterestCostDecimal / (Utilities.power(1 + interestPercent, payments) - 1);   //Calculate decimal separately out of principle.
                                                                                                                            //Can easily be changed
        //Add decimals to Decimal variable
        MonthlyPaymentDecimal += MonthlyPayment % 1 * 100;
        MonthlyPayment -= MonthlyPayment % 1; //Remove decimals from main variable

        //Add back excess decimals to main var (123.456 -> 127.056)
        while(MonthlyPaymentDecimal > 100){
            MonthlyPayment++;
            MonthlyPaymentDecimal -= 100;
        }

        //Round decimal
        if(MonthlyPaymentDecimal % 1 > (double)roundUpOver / 10){ //If decimal is larger than roundUpOver (default 5), round up. 12.7 -> 13
            MonthlyPaymentDecimal++;
        }
        //Now remove excess
        MonthlyPaymentDecimal -= MonthlyPaymentDecimal % 1;

        //Back to ints now that everything is rounded
        return new int[] {(int)MonthlyPayment, (int)MonthlyPaymentDecimal};
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

        //Check if empty
        if(reader.hasNextLine()) {
            reader.nextLine();//Skip first line, it contains column titles
        }
        else{
            System.out.println("Provided file is empty...");
            return new Prospect[]{};
        }

        while(reader.hasNextLine()){
            Prospect newProspect = attemptReadProspect(reader.nextLine());
            if(newProspect.Valid) {
                prospectList.add(newProspect);
            }
        }
        return prospectList.stream().toArray(Prospect[]::new); //https://stackoverflow.com/questions/52720685/no-suitable-method-found-for-arrayliststring-toarraystringnew-in-return
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
            while(line.charAt(i) != '\"' && i <= line.length()){//TODO understand why i <= line.length() always true warning
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
        try {
            toReturn[0] = Integer.parseInt(elements[0]);
            if (elements.length == 2) {
                toReturn[1] = Integer.parseInt(elements[1]);
            }
            //Note toReturn[1] is already set to 0. No need for an else statement
        }catch (NumberFormatException e){
            System.out.printf("Error, tried to read %s as number. Returning zero. Please fix input for reliable results", input);
        }


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