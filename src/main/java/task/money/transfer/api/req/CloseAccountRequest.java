package task.money.transfer.api.req;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class CloseAccountRequest {

    @Min(1)
    @JsonProperty
    private long accountId;

    public CloseAccountRequest() {
    }

    public CloseAccountRequest(long accountId) {
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}
