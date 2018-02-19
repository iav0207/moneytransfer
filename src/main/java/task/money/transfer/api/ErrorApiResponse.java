package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

@ParametersAreNonnullByDefault
public class ErrorApiResponse extends ApiResponse {

    @JsonProperty
    private int errorCode;

    @JsonProperty
    private String detail;

    public ErrorApiResponse() {
        super(Status.ERROR);
    }

    public ErrorApiResponse(int errorCode, String detail) {
        this();
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDetail() {
        return detail;
    }
}
