package task.money.transfer.api.err;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * API error object.
 * No internationalization for the sake of simplicity.
 */
@ParametersAreNonnullByDefault
public class ApiError {

    @JsonProperty
    private final int code;
    @NotNull
    @JsonProperty
    private final String message;

    ApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiError apiError = (ApiError) o;
        return code == apiError.code &&
                Objects.equals(message, apiError.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("message", message)
                .toString();
    }
}
