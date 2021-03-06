package task.money.transfer.api.resp;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class ErrorApiResponse extends ApiResponse {

    @JsonProperty
    private int errorCode;

    @JsonProperty
    private String message;

    ErrorApiResponse() {
        super(Status.ERROR);
    }

    ErrorApiResponse(int errorCode, String message) {
        this();
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
