package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class OpenAccountRequest {

    @Min(1)
    private int currency;

    public OpenAccountRequest(int currency) {
        this.currency = currency;
    }

    public OpenAccountRequest() {
    }

    @JsonProperty("currency")
    public int getCurrency() {
        return currency;
    }
}
