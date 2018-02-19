package task.money.transfer.health;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.health.HealthCheck;
import task.money.transfer.api.ApiResponse;

@ParametersAreNonnullByDefault
public class ApiHealthCheck extends HealthCheck {

    private final Client client;

    public ApiHealthCheck(Client client) {
        this.client = client;
    }

    @Override
    protected Result check() throws Exception {
        ApiResponse apiResponse = client.target("http://localhost:8080/currencies/list")
                .request(MediaType.APPLICATION_JSON).get()
                .readEntity(ApiResponse.class);
        return apiResponse.getStatus() == ApiResponse.Status.OK ?
                Result.healthy() : Result.unhealthy("API returned error response: " + apiResponse);
    }
}
