package task.money.transfer.api;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

@ParametersAreNonnullByDefault
public class MoneyWithdrawRequest {

    @NotNull
    @Min(1)
    @JsonProperty
    private Long accountId;

    @NotNull
    @Min(1)
    @JsonProperty
    private Long amountMicros;

    public MoneyWithdrawRequest() {
    }

    public MoneyWithdrawRequest(Long accountId, Long amountMicros) {
        this.accountId = accountId;
        this.amountMicros = amountMicros;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getAmountMicros() {
        return amountMicros;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoneyWithdrawRequest that = (MoneyWithdrawRequest) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(amountMicros, that.amountMicros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, amountMicros);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountId", accountId)
                .add("amountMicros", amountMicros)
                .toString();
    }
}
