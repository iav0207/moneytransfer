package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class OkApiResponse<T> extends ApiResponse {

    @JsonProperty
    private T body;

    public OkApiResponse() {
        super(Status.OK);
    }

    public OkApiResponse(T body) {
        this();
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
