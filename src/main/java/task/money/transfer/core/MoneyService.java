package task.money.transfer.core;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.Transaction;
import task.money.transfer.api.Account;
import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.db.account.AccountDao;
import task.money.transfer.db.transaction.TransactionDao;

import static task.money.transfer.api.err.ApiErrors.accountInactive;
import static task.money.transfer.api.err.ApiErrors.accountNotFound;
import static task.money.transfer.api.err.ApiErrors.accountsOfDifferentCurrencies;
import static task.money.transfer.api.err.ApiErrors.insufficientFundsToWithdraw;
import static task.money.transfer.api.err.ApiErrors.invalidMoneyAmount;
import static task.money.transfer.api.err.ApiErrors.sameAccount;
import static task.money.transfer.api.resp.ApiResponse.failedBecause;
import static task.money.transfer.api.resp.ApiResponse.success;

@ParametersAreNonnullByDefault
public class MoneyService {

    private final TransactionDao transactions;
    private final AccountDao accounts;

    private final KeyLockManager lockManager = KeyLockManagers.newLock();

    public MoneyService(TransactionDao transactions, AccountDao accounts) {
        this.transactions = transactions;
        this.accounts = accounts;
    }

    /**
     * Get current balance for the specified account.
     * @param accountId     account identifier
     * @return Response holding the account balance micro-money value
     * or an error, if the is no account with the specified id.
     */
    public ApiResponse getBalance(long accountId) {
        return Optional.ofNullable(accounts.findById(accountId))
                .map(acc -> success(transactions.getBalance(accountId)))
                .orElseGet(() -> failedBecause(accountNotFound(accountId)));
    }

    /**
     * Deposit money on the specified account.
     *
     * @param accountId     account identifier
     * @param amount        micro-money value
     * @return Response with deposit transaction info, or an error response.
     * An error may occur if there is no account with the specified id,
     * or the requested deposit amount represents invalid money value.
     */
    public ApiResponse deposit(long accountId, long amount) {
        Account account = accounts.findById(accountId);
        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        Money deposit = Money.valueOfMicros(amount, account.getCurrencyCode());
        if (!deposit.isValidValue() || amount <= 0) {
            return failedBecause(invalidMoneyAmount());
        }
        return success(commit(null, accountId, deposit.micros()));
    }

    /**
     * Withdraw funds from the specified account.
     *
     * @param accountId     identifier of the account to be debited
     * @param amount        micro-money value to withdraw
     * @return Response holding transaction info or an error.<br/>
     * Possible errors include:
     * <ul>
     * <li>invalid money amount requested for withdrawal</li>
     * <li>account does not exist</li>
     * <li>insufficient funds on the account</li>
     * </ul>
     */
    @Transaction(TransactionIsolationLevel.READ_UNCOMMITTED)
    public ApiResponse withdraw(long accountId, long amount) {
        return lockManager.executeLocked(accountId, () -> withdrawSafely(accountId, amount));
    }

    private ApiResponse withdrawSafely(long accountId, long amount) {
        Account account = accounts.findById(accountId);

        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        if (account.getStatus() != Account.Status.ACTIVE) {
            return failedBecause(accountInactive(account));
        }

        Money balance = Money.valueOfMicros(transactions.getBalance(accountId), account.getCurrencyCode());
        Money moneyToWithdraw = Money.valueOfMicros(amount, account.getCurrencyCode());

        if (balance.compareTo(moneyToWithdraw) < 0) {
            return failedBecause(insufficientFundsToWithdraw(accountId, amount));
        }

        if (!moneyToWithdraw.isValidValue() || amount <= 0) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(commit(accountId, null, moneyToWithdraw.micros()));
    }

    /**
     * Transfer money from one specified account to another.
     *
     * @param senderId      identifier of the account to be debited
     * @param recipientId   identifier of the account to be credited
     * @param amount        micro-money amount to transfer between accounts
     * @return Response holding transaction info, or error response.<br/>
     * Possible errors include:
     * <ul>
     * <li>two given ids are the same (meaningless request)</li>
     * <li>invalid money amount requested for transfer</li>
     * <li>at least one of the accounts does not exist</li>
     * <li>accounts are of different currencies</li>
     * <li>insufficient funds on the sender's account</li>
     * </ul>
     */
    @Transaction(TransactionIsolationLevel.READ_UNCOMMITTED)
    public ApiResponse transfer(long senderId, long recipientId, long amount) {
        return lockManager.executeLocked(senderId, () -> transferSafely(senderId, recipientId, amount));
    }

    private ApiResponse transferSafely(long senderId, long recipientId, long amount) {
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

        if (!money.isValidValue() || amount <= 0) {
            return failedBecause(invalidMoneyAmount());
        }

        return success(commit(senderId, recipientId, money.micros()));
    }

    private task.money.transfer.api.Transaction commit(@Nullable Long senderId, @Nullable Long recipientId,
            long micros)
    {
        long trxId = transactions.add(senderId, recipientId, micros);
        return transactions.getById(trxId);
    }

}
