package task.money.transfer.core;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Core object: money.
 * <p>
 * Money pattern implementation which is also convenient
 * for API <i>micro-money</i> representation.
 */
@ParametersAreNonnullByDefault
public class Money implements Comparable<Money> {

    @SuppressWarnings("WeakerAccess")
    public static final MathContext MONEY_MATH_CONTEXT = MathContext.DECIMAL128;
    /**
     * Used to represent all money values as integers in the API.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int MICRO_MULTIPLIER_SCALE = 6;
    /**
     * Decimal places.
     * <p>
     * In fact, it is a property of currencies. But now there are only few currencies supported,
     * so for simplicity the value is hardcoded.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int MONEY_CENT_SCALE = 2;

    private final BigDecimal value;
    private final int currencyCode;

    public Money(BigDecimal value, int currencyCode) {
        this.value = value;
        this.currencyCode = currencyCode;
    }

    public static Money valueOf(BigDecimal value, int currencyCode) {
        checkNotNull(value, "Value must be non-null");
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

    /**
     * Get {@link Money} instance, if micro-money value is known.
     * <p>
     * Micro-money are used in API.
     */
    public static Money valueOfMicros(long micros, int currencyCode) {
        return valueOf(BigDecimal.valueOf(micros, MICRO_MULTIPLIER_SCALE), currencyCode);
    }

    public BigDecimal bigDecimalValue() {
        return value;
    }

    /**
     * @return Micro-money (long) value, i.e. original money amount multiplied by 10^{@value #MICRO_MULTIPLIER_SCALE}.
     * @see #MICRO_MULTIPLIER_SCALE
     */
    public long micros() {
        return value.scaleByPowerOfTen(MICRO_MULTIPLIER_SCALE).longValue();
    }

    /**
     * @return {@code true}, if this instance represents a valid amount of money,
     * i.e. amount greater than <i>cent scale</i>.
     */
    public boolean isValidValue() {
        return value.scaleByPowerOfTen(MONEY_CENT_SCALE).remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    public Money add(Money that) {
        checkArgument(that.currencyCode == currencyCode,
                "Can't calculate money in different currencies");
        return Money.valueOf(value.add(that.value, MONEY_MATH_CONTEXT), currencyCode);
    }

    public Money subtract(Money that) {
        checkArgument(that.currencyCode == currencyCode,
                "Can't calculate money in different currencies");
        return Money.valueOf(value.subtract(that.value, MONEY_MATH_CONTEXT), currencyCode);
    }

    @Override
    public int compareTo(Money that) {
        checkArgument(this.currencyCode == that.currencyCode);
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

        if (currencyCode != money.currencyCode) {
            return false;
        }
        return value.equals(money.value);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + currencyCode;
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("currencyCode", currencyCode)
                .toString();
    }
}
