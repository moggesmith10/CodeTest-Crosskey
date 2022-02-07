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
    void attemptReadDecimal() {
    }
}