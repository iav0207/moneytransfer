package task.money.transfer.api;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.google.common.base.Preconditions.checkArgument;

@JsonInclude(NON_NULL)
@ParametersAreNonnullByDefault
public class Transaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name())
                    .toString();
        }
    }

    @NotNull
    @JsonProperty
    private Type type;

    @JsonProperty
    private Long sender;

    @JsonProperty
    private Long recipient;

    @Min(0)
    @JsonProperty
    private long amount;

    @NotNull
    @JsonProperty
    private DateTime timestamp;

    public Transaction() {
    }

    public Transaction(@Nullable Long sender, @Nullable Long recipient, long amount, DateTime timestamp) {
        checkArgument(sender != null || recipient != null);

        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;

        if (sender == null) {
            this.type = Type.DEPOSIT;
        } else if (recipient == null) {
            this.type = Type.WITHDRAWAL;
        } else {
            this.type = Type.TRANSFER;
        }
    }

    public Type getType() {
        return type;
    }

    public Long getSender() {
        return sender;
    }

    public Long getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return amount == that.amount &&
                type == that.type &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(recipient, that.recipient) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, sender, recipient, amount, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("sender", sender)
                .add("recipient", recipient)
                .add("amount", amount)
                .add("timestamp", timestamp)
                .toString();
    }
}
