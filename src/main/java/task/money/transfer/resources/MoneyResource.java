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

import task.money.transfer.api.ApiResponse;
import task.money.transfer.api.MoneyTransferRequest;
import task.money.transfer.core.Money;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.TransactionDao;

import static task.money.transfer.api.ApiResponse.failedBecause;
import static task.money.transfer.api.ApiResponse.success;
import static task.money.transfer.api.err.ApiErrors.accountNotFound;

@Path("/money")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class MoneyResource {

    private final AccountDao accounts;
    private final TransactionDao transactions;

    public MoneyResource(AccountDao accounts, TransactionDao transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
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
    public ApiResponse deposit(/* TODO MoneyDepositRequest */) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/transfer")
    public ApiResponse transfer(@Valid MoneyTransferRequest req) {
        Long sender = req.getSenderAccountId();
        Long recipient = req.getRecipientAccountId();
        Money money = Money.valueOfMicros(req.getAmountMicros(), 0/* TODO getCurrency */);
        // TODO check micros amount greater than min val of 10^(6-2)
        // TODO check both accounts exist,  active, and have same currency
        // TODO check sender has enough funds
        return success(transactions.add(sender, recipient, money.micros()));
    }
}
