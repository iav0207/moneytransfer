package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class OkApiResponse<T> extends ApiResponse {

    @JsonProperty
    private T body;

    OkApiResponse() {
        super(Status.OK);
    }

    OkApiResponse(T body) {
        this();
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
