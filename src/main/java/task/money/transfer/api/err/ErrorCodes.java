package task.money.transfer.api.err;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class ErrorCodes {
    private ErrorCodes() {
    }

    static final int OBJECT_NOT_FOUND = 8000;
}
