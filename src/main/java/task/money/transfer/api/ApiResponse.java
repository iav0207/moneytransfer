package task.money.transfer.api;

import javax.annotation.ParametersAreNonnullByDefault;

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

    public Status getStatus() {
        return status;
    }
}
