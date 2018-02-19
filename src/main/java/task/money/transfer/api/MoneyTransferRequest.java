package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class MoneyTransferRequest {

    @NotNull
    @JsonProperty
    private Long senderAccountId;

    @NotNull
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
}
