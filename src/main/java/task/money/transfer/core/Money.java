package task.money.transfer.core;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@ParametersAreNonnullByDefault
public class Money implements Comparable<Money> {

    @SuppressWarnings("WeakerAccess")
    public static final MathContext MONEY_MATH_CONTEXT = MathContext.DECIMAL128;

    private final BigDecimal value;
    private final int currency;

    public Money(BigDecimal value, int currency) {
        this.value = value;
        this.currency = currency;
    }

    public static Money valueOf(BigDecimal value, int currencyCode) {
        checkNotNull(value, "Can't create money from null");
        return new Money(value, currencyCode);
    }

    /**
     * @return {@link Money}, corresponding to given {@code value} and {@code currencyCode}
     */
    public static Money valueOf(double value, int currencyCode) {
        return valueOf(Double.toString(value), currencyCode);
    }

    /**
     * @return {@link Money}, corresponding to given {@code value} and {@code currencyCode}
     */
    public static Money valueOf(String value, int currencyCode) {
        return valueOf(new BigDecimal(value, MONEY_MATH_CONTEXT), currencyCode);
    }

    public BigDecimal bigDecimalValue() {
        return value;
    }

    @Override
    public int compareTo(Money that) {
        checkArgument(this.currency == that.currency);
        return this.value.compareTo(that.value);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Money money = (Money) that;

        if (currency != money.currency) {
            return false;
        }
        return value.equals(money.value);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + currency;
        return result;
    }
}
