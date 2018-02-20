package task.money.transfer.resources;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import task.money.transfer.api.Account;
import task.money.transfer.api.ApiResponse;
import task.money.transfer.api.MoneyDepositRequest;
import task.money.transfer.api.MoneyTransferRequest;
import task.money.transfer.api.MoneyWithdrawRequest;
import task.money.transfer.core.Money;
import task.money.transfer.core.TransactionsRepository;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.TransactionDao;

import static task.money.transfer.api.ApiResponse.failedBecause;
import static task.money.transfer.api.ApiResponse.success;
import static task.money.transfer.api.err.ApiErrors.accountNotFound;
import static task.money.transfer.api.err.ApiErrors.invalidMoneyAmount;

@Path("/money")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class MoneyResource {

    private final AccountDao accounts;
    private final TransactionDao transactions;
    private final TransactionsRepository doInTransaction;

    public MoneyResource(AccountDao accountDao, TransactionDao transactionDao, TransactionsRepository repository) {
        this.accounts = accountDao;
        this.transactions = transactionDao;
        this.doInTransaction = repository;
    }

    @GET
    @Path("/balance")
    public ApiResponse balance(@QueryParam("accountId") long accountId) {
        return Optional.ofNullable(accounts.findById(accountId))
                .map(acc -> success(transactions.getBalance(accountId)))
                .orElseGet(() -> failedBecause(accountNotFound(accountId)));
    }

    @POST
    @Path("/deposit")
    public ApiResponse deposit(MoneyDepositRequest req) {
        long accountId = req.getAccountId();
        Account account = accounts.findById(accountId);
        if (account == null) {
            return failedBecause(accountNotFound(accountId));
        }
        Money deposit = Money.valueOfMicros(req.getAmountMicros(), account.getCurrencyCode());
        if (!deposit.isValidValue()) {
            return failedBecause(invalidMoneyAmount());
        }
        return success(transactions.add(null, req.getAccountId(), deposit.micros()));
    }

    @POST
    @Path("/withdraw")
    public ApiResponse withdraw(MoneyWithdrawRequest req) {
        return doInTransaction.withdraw(req.getAccountId(), req.getAmountMicros());
    }

    @POST
    @Path("/transfer")
    public ApiResponse transfer(@Valid MoneyTransferRequest req) {
        return doInTransaction.transfer(req.getSenderAccountId(), req.getRecipientAccountId(), req.getAmountMicros());
    }
}
