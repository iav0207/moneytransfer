package task.money.transfer.api.req;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

@ParametersAreNonnullByDefault
public class MoneyTransferRequest {

    @NotNull
    @Min(1)
    @JsonProperty
    private Long senderAccountId;

    @NotNull
    @Min(1)
    @JsonProperty
    private Long recipientAccountId;

    @NotNull
    @Min(1)
    @JsonProperty
    private Long amountMicros;

    public MoneyTransferRequest() {
    }

    public MoneyTransferRequest(Long senderAccountId, Long recipientAccountId, Long amountMicros) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.amountMicros = amountMicros;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public Long getRecipientAccountId() {
        return recipientAccountId;
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
        MoneyTransferRequest that = (MoneyTransferRequest) o;
        return Objects.equals(senderAccountId, that.senderAccountId) &&
                Objects.equals(recipientAccountId, that.recipientAccountId) &&
                Objects.equals(amountMicros, that.amountMicros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderAccountId, recipientAccountId, amountMicros);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senderAccountId", senderAccountId)
                .add("recipientAccountId", recipientAccountId)
                .add("amountMicros", amountMicros)
                .toString();
    }
}
