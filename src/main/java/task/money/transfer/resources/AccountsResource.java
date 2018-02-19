package task.money.transfer.resources;

import java.util.NoSuchElementException;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import task.money.transfer.api.Account;
import task.money.transfer.api.OpenAccountRequest;
import task.money.transfer.db.AccountDao;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class AccountsResource {

    private final AccountDao dao;

    public AccountsResource(AccountDao dao) {
        this.dao = dao;
    }

    @GET
    public Account getById(@QueryParam("id") long id) {
        return dao.findById(id).orElseThrow(NoSuchElementException::new);   // TODO response
    }

    @POST
    @Path("/open")
    public Account open(@Valid OpenAccountRequest req) {
        // TODO validate currency code existent
        long newAccountId = dao.createAccount(req.getCurrency());
        return dao.findById(newAccountId).orElseThrow(IllegalStateException::new);
    }
}
