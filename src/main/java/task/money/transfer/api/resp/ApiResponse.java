package task.money.transfer.api.resp;

import javax.annotation.ParametersAreNonnullByDefault;

import task.money.transfer.api.err.ApiError;

/**
 * Basic DTO returned by all API resources' methods.
 */
@ParametersAreNonnullByDefault
public abstract class ApiResponse {

    public enum Status {
        OK,
        ERROR,
    }

    private Status status;

    public ApiResponse() {
    }

    public ApiResponse(Status status) {
        this.status = status;
    }

    public static ApiResponse success() {
        return new OkApiResponse<>();
    }

    public static <T> ApiResponse success(T body) {
        return new OkApiResponse<>(body);
    }

    public static ApiResponse failedBecause(ApiError error) {
        return new ErrorApiResponse(error.getCode(), error.getMessage());
    }

    public Status getStatus() {
        return status;
    }
}
