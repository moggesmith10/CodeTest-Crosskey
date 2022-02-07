import javax.swing.*;
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
        Prospect[] prospects;
        try{
            prospects = getProspects(args[0]);
        }
        catch ( FileNotFoundException error){
            System.out.printf("File \"%s\" not found. Exiting...%n", args[0]);
            System.exit(0);
        }
    }

    static Prospect[] getProspects(String fileName) throws FileNotFoundException {
        List<Prospect> prospectList = new ArrayList<>();

        File inputFile = new File(fileName);
        Scanner reader = new Scanner(inputFile);

        while(reader.hasNextLine()){
            Prospect newProspect = attemptReadProspect(reader.nextLine());
            if(newProspect.Valid) {
                prospectList.add(newProspect);
            }
        }

        return prospectList.toArray(Prospect[]::new);
    }

    static Prospect attemptReadProspect(String line){

        //If line contains '"', it means the name of prospect might contain a comma.
        List<Integer> commasInName = getCommas(line);
        //Temporarily remove commas in name
        for(Integer pos: commasInName){
            line = Utilities.removeAtPosition(line, pos);
        }

        String[] elements = line.split(",");

        int[] totalLoan = attemptReadDecimal(elements[1]);
        int[] interest = attemptReadDecimal(elements[2]);
        int years = Integer.parseInt(elements[3]);

        return new Prospect(reFormatName(elements[0], commasInName) //Name
                , totalLoan[0], totalLoan[1]                        //Total loan
                , interest[0], interest[1]                          //Interest
                , years                                             //Years
                , Boolean.TRUE);                                    //Valid
    }

    /**
     * Get list of all commas in string in quotations
     * @param line String to read
     * @return List of commas
     */
    static List<Integer> getCommas(String line){
        List<Integer> commasInName = new ArrayList<>();

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
            elements = input.split(decimalDenominator);
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