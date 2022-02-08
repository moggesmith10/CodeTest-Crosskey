import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

class MortagePlanTest {

    @Test
    void getCommasCheckWithCommas() {
        List<Integer> verify = new ArrayList<>();
        verify.add(5);
        Assertions.assertEquals(verify, Mortage_Plan.getCommas("\"test,\","));
    }
    @Test
    void getCommasCheckWithoutCommas(){
        List<Integer> verify = new ArrayList<>();
        Assertions.assertEquals(verify, Mortage_Plan.getCommas("Normal name, 123"));
    }
    @Test
    void reFormatName() {
        List<Integer> verify = new ArrayList<>();
        verify.add(4);
        String testName = Mortage_Plan.reFormatName("test name", verify);
        Assertions.assertEquals(testName, "test, name");
    }
    @Test
    void testEntireFormatting(){
        String name = "\"Sir, test name\", 123";
        String toVerify = name;
        List<Integer> verify = Mortage_Plan.getCommas(name);
        for(Integer pos: verify){
            name = Utilities.removeAtPosition(name, pos);
        }
        name = Mortage_Plan.reFormatName(name,verify);

        Assertions.assertEquals(name, toVerify);
    }
    @Test
    void testReadSimpleProspect(){
        Prospect prospect = new Prospect("Juha", 1000, 0,5,0, 2, Boolean.TRUE);
        Prospect newProspect = Mortage_Plan.attemptReadProspect("Juha,1000,5,2");

        Assertions.assertEquals(prospect, newProspect);
    }
    @Test
    void testReadComplicatedProspect(){
        Prospect prospect = new Prospect("\"Clarencé,Andersson\"", 2000, 0,6,87, 4, Boolean.TRUE);
        Prospect newProspect = Mortage_Plan.attemptReadProspect("\"Clarencé,Andersson\",2000,6.87,4");

        Assertions.assertEquals(prospect, newProspect);
    }
    @Test
    void testMortageCalculations(){                                                               //Note Clarence normally has 6,0% interest, testing 6,87% to test decimals
        Prospect prospect = new Prospect("\"Clarencé,Andersson\"", 2000, 0,6,87, 4, Boolean.TRUE);
        int[] test = Mortage_Plan.CalculateMonthlyPayment(prospect);

        Assertions.assertTrue(test[0] == 143 && test[1] == 30);
        /*
        2000(0,0687(1,0687)^48) / 1,0687^48 - 1
        = 2000(0,0687 * 24,27057) / 23,27057
        = 2000 * 1,66738 / 23,27057
        = 3334,76 / 23,27057
        = 143,3037
        =~143,30
         */
    }
    @Test
    void attemptReadDecimalWithDecimal() {
        int[] decimals = Mortage_Plan.attemptReadDecimal("12.3");
        Assertions.assertTrue(decimals[0] == 12 && decimals[1] == 3);
    }
    @Test
    void attemptReadDecimalWithoutDecimal() {
        int[] decimals = Mortage_Plan.attemptReadDecimal("12");
        Assertions.assertTrue(decimals[0] == 12 && decimals[1] == 0);
    }
    @Test
    void powerTest(){
        Assertions.assertEquals(Utilities.power(5,2), 25);
        Assertions.assertEquals(Utilities.power(2,4), 16);
        Assertions.assertEquals(Utilities.power(25,0), 1);
        Assertions.assertEquals(Utilities.power(1,1), 1);
        Assertions.assertEquals(Utilities.power(1,4), 1);
    }
    @Test
    void removeAtPositionTest(){
        String string1 = "abcd";
        String string2 = Utilities.removeAtPosition("abxcd", 2);
        Assertions.assertEquals(string1, string2);
    }
    @Test
    void tryGetInputDirectoryTest(){
        String input = Mortage_Plan.getInputFileDirectory(new String[]{"input"});
        Assertions.assertEquals("input", input);
    }
}