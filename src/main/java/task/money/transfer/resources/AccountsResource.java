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

import task.money.transfer.api.req.OpenAccountRequest;
import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.db.account.AccountDao;
import task.money.transfer.db.currency.CurrencyDao;

import static task.money.transfer.api.err.ApiErrors.accountNotFound;
import static task.money.transfer.api.err.ApiErrors.currencyIsNotSupported;
import static task.money.transfer.api.resp.ApiResponse.failedBecause;
import static task.money.transfer.api.resp.ApiResponse.success;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class AccountsResource {

    private final AccountDao accounts;
    private final CurrencyDao currencies;

    public AccountsResource(AccountDao accounts, CurrencyDao currencies) {
        this.accounts = accounts;
        this.currencies = currencies;
    }

    @GET
    public ApiResponse getById(@QueryParam("id") long id) {
        return Optional.ofNullable(accounts.findById(id))
                .map(ApiResponse::success)
                .orElseGet(() -> failedBecause(accountNotFound(id)));
    }

    @POST
    @Path("/open")
    public ApiResponse open(@Valid OpenAccountRequest req) {
        int currencyCode = req.getCurrency();
        if (!currencies.isSupported(currencyCode)) {
            return failedBecause(currencyIsNotSupported(currencyCode));
        }

        long newAccountId = accounts.createAccount(currencyCode);
        return success(accounts.findById(newAccountId));
    }
}
