package task.money.transfer.core;

import java.math.BigDecimal;

import javax.annotation.ParametersAreNonnullByDefault;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class MoneyTest {

    private static final int CURR_1 = 1;
    private static final int CURR_2 = 2;

    @Test(dataProvider = "equality")
    public void instancesEqualityTest(Money first, Money second, boolean expectedEqual) {
        assertEquals(first.equals(second), expectedEqual);
    }

    @DataProvider(name = "equality")
    public static Object[][] equality() {
        return new Object[][]{
                {
                        Money.valueOf(11.03, CURR_1),
                        Money.valueOf("11.03", CURR_1),
                        true
                },
                {
                        Money.valueOf(11.03, CURR_1),
                        Money.valueOf(new BigDecimal("11.03"), CURR_1),
                        true
                },
                {
                        Money.valueOf(0, CURR_1),
                        Money.valueOf(0, CURR_2),
                        false
                },
                {
                        Money.valueOf(100.01, CURR_1),
                        Money.valueOf(100.010001, CURR_1),
                        false
                },
        };
    }

}
