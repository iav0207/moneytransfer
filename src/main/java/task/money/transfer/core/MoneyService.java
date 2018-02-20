package task.money.transfer.core;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.Transaction;
import task.money.transfer.api.Account;
import task.money.transfer.api.ApiResponse;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.TransactionDao;

import static task.money.transfer.api.ApiResponse.failedBecause;
import static task.money.transfer.api.ApiResponse.success;
import static task.money.transfer.api.err.ApiErrors.accountInactive;
import static task.money.transfer.api.err.ApiErrors.accountNotFound;
import static task.money.transfer.api.err.ApiErrors.accountsOfDifferentCurrencies;
import static task.money.transfer.api.err.ApiErrors.insufficientFundsToWithdraw;
import static task.money.transfer.api.err.ApiErrors.invalidMoneyAmount;
import static task.money.transfer.api.err.ApiErrors.sameAccount;

@ParametersAreNonnullByDefault
public class MoneyService {

    private final TransactionDao transactions;
    private final AccountDao accounts;

    public MoneyService(TransactionDao transactions, AccountDao accounts) {
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public ApiResponse getBalance(long accountId) {
        return Optional.ofNullable(accounts.findById(accountId))
                .map(acc -> success(transactions.getBalance(accountId)))
                .orElseGet(() -> failedBecause(accountNotFound(accountId)));
    }

    public ApiResponse deposit(long accountId, long amount) {
        Account account = accounts.findById(accountId);
        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        Money deposit = Money.valueOfMicros(amount, account.getCurrencyCode());
        if (!deposit.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }
        return success(transactions.add(null, accountId, deposit.micros()));
    }

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    public ApiResponse withdraw(long accountId, long amount) {
        Account account = accounts.findById(accountId);

        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        if (account.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(account));
        }

        long balance = transactions.getBalance(accountId);

        if (balance < amount) {
            return failedBecause(insufficientFundsToWithdraw(accountId, amount));
        }

        Money money = Money.valueOfMicros(amount, account.getCurrencyCode());

        if (!money.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(transactions.add(accountId, null, money.micros()));
    }

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    public ApiResponse transfer(long senderId, long recipientId, long amount) {
        if (senderId == recipientId) {
            return failedBecause(sameAccount());
        }

        Account sender = accounts.findById(senderId);

        if (sender == null) {
            return failedBecause(accountNotFound(senderId));
        }
        if (sender.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(sender));
        }

        Account recipient = accounts.findById(recipientId);

        if (recipient == null) {
            return failedBecause(accountNotFound(recipientId));
        }
        if (recipient.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(recipient));
        }

        if (!sender.getCurrencyCode().equals(recipient.getCurrencyCode())) {
            return failedBecause(accountsOfDifferentCurrencies());
        }

        if (transactions.getBalance(senderId) < amount) {
            return failedBecause(insufficientFundsToWithdraw(senderId, amount));
        }

        Money money = Money.valueOfMicros(amount, sender.getCurrencyCode());

        if (!money.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(transactions.add(senderId, recipientId, money.micros()));
    }

}
