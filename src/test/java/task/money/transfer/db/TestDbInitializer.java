package task.money.transfer.db;

import javax.annotation.ParametersAreNonnullByDefault;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import task.money.transfer.db.account.AccountDao;
import task.money.transfer.db.account.AccountMapper;
import task.money.transfer.db.currency.CurrenciesInitializer;
import task.money.transfer.db.currency.CurrencyDao;
import task.money.transfer.db.currency.CurrencyMapper;
import task.money.transfer.db.transaction.TransactionDao;
import task.money.transfer.db.transaction.TransactionMapper;

@ParametersAreNonnullByDefault
public class TestDbInitializer {

    private static final String DRIVER = "org.h2.Driver";
    private static final String URL = "jdbc:h2:mem:testDb";

    private static final Environment env = new Environment( "test-env", Jackson.newObjectMapper(),
            null, new MetricRegistry(), null);

    private static final DataSourceFactory dataSourceFactory = createDataSourceFactory();

    private static final DBI dbi = new DBIFactory().build(env, dataSourceFactory, "test");

    static {
        CurrencyDao currencies = dbi.onDemand(CurrencyDao.class);
        AccountDao accounts = dbi.onDemand(AccountDao.class);
        TransactionDao transactions = dbi.onDemand(TransactionDao.class);

        currencies.createTableIfNotExists();
        accounts.createTableIfNotExists();
        transactions.createTableIfNotExists();

        new CurrenciesInitializer(currencies).populateCurrencies();

        dbi.registerMapper(new CurrencyMapper());
        dbi.registerMapper(new AccountMapper());
        dbi.registerMapper(new TransactionMapper());
    }

    private static DataSourceFactory createDataSourceFactory() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass(DRIVER);
        dataSourceFactory.setUrl(URL);
        return dataSourceFactory;
    }

    public static DBI getDbi() {
        return dbi;
    }
}
