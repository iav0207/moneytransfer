package task.money.transfer.api.err;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ErrorCodes {
    private ErrorCodes() {
    }

    public static final int OBJECT_NOT_FOUND = 8000;

    public static final int INSUFFICIENT_FUNDS = 6000;

    public static final int INCONSISTENT_STATE = 6002;

    public static final int INVALID_USE_OF_FIELD = 5000;
}
