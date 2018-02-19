package task.money.transfer.api.err;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.String.format;
import static task.money.transfer.api.err.ErrorCodes.OBJECT_NOT_FOUND;

@ParametersAreNonnullByDefault
public class ApiErrors {

    private ApiErrors() {
    }

    public static ApiError accountNotFound(long id) {
        return new ApiError(OBJECT_NOT_FOUND, format("There is no account with id=%d.", id));
    }

    public static ApiError currencyIsNotSupported(int code) {
        return new ApiError(OBJECT_NOT_FOUND, format("Currency (code=%d) is not supported.", code));
    }

}
