package task.money.transfer.api;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.Length;

@ParametersAreNonnullByDefault
public class Currency {

    @Min(1)
    @JsonProperty
    private Integer numCode;

    @NotNull
    @Length(min = 3, max = 3)
    @JsonProperty
    private String isoCode;

    public Currency() {
    }

    public Currency(Integer numCode, String isoCode) {
        this.numCode = numCode;
        this.isoCode = isoCode;
    }

    public Integer getNumCode() {
        return numCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Currency currency = (Currency) o;
        return Objects.equals(numCode, currency.numCode) &&
                Objects.equals(isoCode, currency.isoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numCode, isoCode);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("numCode", numCode)
                .add("isoCode", isoCode)
                .toString();
    }
}
