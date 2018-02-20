package task.money.transfer.resources;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import task.money.transfer.api.req.MoneyDepositRequest;
import task.money.transfer.api.req.MoneyTransferRequest;
import task.money.transfer.api.req.MoneyWithdrawRequest;
import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.core.MoneyService;

@Path("/money")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class MoneyResource {

    private final MoneyService delegate;

    public MoneyResource(MoneyService moneyService) {
        this.delegate = moneyService;
    }

    @GET
    @Path("/balance")
    public ApiResponse balance(@QueryParam("accountId") long accountId) {
        return delegate.getBalance(accountId);
    }

    @POST
    @Path("/deposit")
    public ApiResponse deposit(@Valid MoneyDepositRequest req) {
        return delegate.deposit(req.getAccountId(), req.getAmountMicros());
    }

    @POST
    @Path("/withdraw")
    public ApiResponse withdraw(@Valid MoneyWithdrawRequest req) {
        return delegate.withdraw(req.getAccountId(), req.getAmountMicros());
    }

    @POST
    @Path("/transfer")
    public ApiResponse transfer(@Valid MoneyTransferRequest req) {
        return delegate.transfer(req.getSenderAccountId(), req.getRecipientAccountId(), req.getAmountMicros());
    }
}
