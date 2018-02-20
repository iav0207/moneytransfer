package task.money.transfer.core;

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
public class TransactionsRepository {

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

    public TransactionsRepository(TransactionDao transactionDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    public ApiResponse withdraw(long accountId, long amount) {
        Account account = accountDao.findById(accountId);

        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        if (account.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(accountId, account.getStatus()));
        }

        long balance = transactionDao.getBalance(accountId);

        if (balance < amount) {
            return failedBecause(insufficientFundsToWithdraw(accountId, amount));
        }

        Money money = Money.valueOfMicros(amount, account.getCurrencyCode());

        if (!money.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(transactionDao.add(accountId, null, money.micros()));
    }

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    public ApiResponse transfer(long senderId, long recipientId, long amount) {
        if (senderId == recipientId) {
            return failedBecause(sameAccount());
        }

        Account sender = accountDao.findById(senderId);

        if (sender == null) {
            return failedBecause(accountNotFound(senderId));
        }
        if (sender.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(senderId, sender.getStatus()));
        }

        Account recipient = accountDao.findById(recipientId);

        if (recipient == null) {
            return failedBecause(accountNotFound(recipientId));
        }
        if (recipient.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(recipientId, recipient.getStatus()));
        }

        if (!sender.getCurrencyCode().equals(recipient.getCurrencyCode())) {
            return failedBecause(accountsOfDifferentCurrencies());
        }

        if (transactionDao.getBalance(senderId) < amount) {
            return failedBecause(insufficientFundsToWithdraw(senderId, amount));
        }

        Money money = Money.valueOfMicros(amount, sender.getCurrencyCode());

        if (!money.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(transactionDao.add(senderId, recipientId, money.micros()));
    }

}
