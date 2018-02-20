package task.money.transfer.api.err;

import javax.annotation.ParametersAreNonnullByDefault;

import task.money.transfer.api.Account;

import static java.lang.String.format;
import static task.money.transfer.api.err.ErrorCodes.INCONSISTENT_STATE;
import static task.money.transfer.api.err.ErrorCodes.INSUFFICIENT_FUNDS;
import static task.money.transfer.api.err.ErrorCodes.INVALID_USE_OF_FIELD;
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

    public static ApiError insufficientFundsToWithdraw(long account, long microsRequested) {
        return new ApiError(INSUFFICIENT_FUNDS,
                format("Not enough funds to withdraw %d micros from account %d.", microsRequested, account));
    }

    public static ApiError accountInactive(Account account) {
        return accountInactive(account.getId(), account.getStatus());
    }

    public static ApiError accountInactive(long accountId, Account.Status status) {
        return new ApiError(INCONSISTENT_STATE, format("Account %d is %s.", accountId, status.name()));
    }

    public static ApiError accountsOfDifferentCurrencies() {
        return new ApiError(INCONSISTENT_STATE, "Accounts have different currencies");
    }

    public static ApiError sameAccount() {
        return new ApiError(INVALID_USE_OF_FIELD, "Accounts should be distinct");
    }

    public static ApiError invalidMoneyAmount() {
        return new ApiError(INVALID_USE_OF_FIELD, "Invalid value: money amount");
    }

}
