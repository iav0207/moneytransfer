package task.money.transfer.resources;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.db.CurrencyDao;

import static task.money.transfer.api.resp.ApiResponse.success;

@Path("/currencies")
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class CurrenciesResource {

    private final CurrencyDao dao;

    public CurrenciesResource(CurrencyDao dao) {
        this.dao = dao;
    }

    @GET
    @Path("/list")
    public ApiResponse list() {
        return success(dao.getAllSupportedCurrencies());
    }
}
