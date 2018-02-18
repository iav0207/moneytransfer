package task.money.transfer.health;

import javax.annotation.ParametersAreNonnullByDefault;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.DataSourceFactory;

@ParametersAreNonnullByDefault
public class DatabaseHealthCheck extends HealthCheck {

    private final DataSourceFactory dataSourceFactory;

    public DatabaseHealthCheck(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    protected Result check() throws Exception {
        if (!"org.h2.Driver".equals(dataSourceFactory.getDriverClass())) {
            return HealthCheck.Result.unhealthy("DB hasn't been initialized properly");
        }
        return HealthCheck.Result.healthy();
    }
}
