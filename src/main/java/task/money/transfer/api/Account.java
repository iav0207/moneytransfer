package task.money.transfer.api;

import java.util.NoSuchElementException;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

@ParametersAreNonnullByDefault
public class Account {

    public enum Status {
        ACTIVE,
        SUSPENDED,
        CLOSED;

        public static Status fromString(String s) {
            for (Status val : values()) {
                if (val.name().equalsIgnoreCase(s)) {
                    return val;
                }
            }
            throw new NoSuchElementException(s);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name())
                    .toString();
        }
    }

    private long id;

    private int currencyCode;

    private Status status;

    public Account() {
    }

    public Account(long id, int currencyCode, Status status) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.status = status;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public int getCurrencyCode() {
        return currencyCode;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Account account = (Account) o;

        if (id != account.id) {
            return false;
        }
        if (currencyCode != account.currencyCode) {
            return false;
        }
        return status == account.status;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + currencyCode;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("currencyCode", currencyCode)
                .add("status", status)
                .toString();
    }
}
