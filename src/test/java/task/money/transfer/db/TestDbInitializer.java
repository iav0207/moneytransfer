package task.money.transfer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

@ParametersAreNonnullByDefault
public class TestDbInitializer {

    private static final String DRIVER = "org.h2.Driver";
    private static final String URL = "jdbc:h2:mem:testDb";

    private static final Environment env = new Environment( "test-env", Jackson.newObjectMapper(),
            null, new MetricRegistry(), null);

    private static final DataSourceFactory dataSourceFactory = createDataSourceFactory();

    private static final DBI dbi = new DBIFactory().build(env, dataSourceFactory, "test" );

    private static DataSourceFactory createDataSourceFactory() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass(DRIVER);
        dataSourceFactory.setUrl(URL);
        return dataSourceFactory;
    }

    public static Environment getEnv() {
        return env;
    }

    public static DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public static DBI getDbi() {
        return dbi;
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get connection to db.", e);
        }
    }
}
