package task.money.transfer.core;

import java.math.BigDecimal;

import javax.annotation.ParametersAreNonnullByDefault;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

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
                {
                        Money.valueOf("100", CURR_1),
                        Money.valueOf("100.000", CURR_1),
                        false
                },
        };
    }

    @Test
    public void testAddImmutability() {
        BigDecimal firstDec = new BigDecimal("0.3");
        Money first = Money.valueOf(firstDec, CURR_1);
        Money second = Money.valueOf(0.1, CURR_1);

        assertNotSame(first, first.add(second));
        assertEquals(first.bigDecimalValue(), firstDec);
    }

    @Test
    public void testAddSymmetry() {
        Money first = Money.valueOf(0.37, CURR_1);
        Money second = Money.valueOf(0.11, CURR_1);

        assertEquals(first.add(second), second.add(first));
    }

    @Test
    public void testAdd() {
        BigDecimal firstDec = new BigDecimal("0.37");
        BigDecimal secondDec = new BigDecimal("0.1101");
        Money first = Money.valueOf(firstDec, CURR_1);
        Money second = Money.valueOf(secondDec, CURR_1);

        assertEquals(first.add(second).bigDecimalValue(), firstDec.add(secondDec));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionOnAddDifferentCurrencies() {
        Money.valueOf(0.1, CURR_1).add(Money.valueOf(1, CURR_2));
    }

    @Test
    public void testSubtractImmutability() {
        BigDecimal firstDec = new BigDecimal("0.3");
        Money first = Money.valueOf(firstDec, CURR_1);
        Money second = Money.valueOf(0.1, CURR_1);

        assertNotSame(first, first.subtract(second));
        assertEquals(first.bigDecimalValue(), firstDec);
    }

    @Test
    public void testSubtract() {
        BigDecimal firstDec = new BigDecimal("0.37");
        BigDecimal secondDec = new BigDecimal("0.1101");
        Money first = Money.valueOf(firstDec, CURR_1);
        Money second = Money.valueOf(secondDec, CURR_1);

        assertEquals(first.subtract(second).bigDecimalValue(), firstDec.subtract(secondDec));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionOnSubtractDifferentCurrencies() {
        Money.valueOf(0.1, CURR_1).add(Money.valueOf(1, CURR_2));
    }

    @Test
    public void checkMicroMoneyScaling() {
        assertEquals(Money.valueOf("100.000000", CURR_1), Money.valueOfMicros(100_000_000, CURR_1));
    }

    @Test
    public void checkMicros() {
        assertEquals(Money.valueOf(10.123_456_789, CURR_1).micros(), 10_123_456);
    }

    @Test(dataProvider = "isValid")
    public void checkIsValidValue(double val, boolean expected) {
        assertEquals(Money.valueOf(val, CURR_1).isValidValue(), expected);
    }

    @DataProvider(name = "isValid")
    public static Object[][] validDp() {
        return new Object[][] {
                {1, true},
                {0.1, true},
                {0.01, true},
                {0.011, false},
                {0.0095, false},
                {10.007, false},
                {0.0001, false},
                {0, true},
                {-1, true},
                {-1.003, false},
        };
    }
}
