package task.money.transfer;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.CurrenciesInitializer;
import task.money.transfer.db.CurrencyDao;
import task.money.transfer.db.TransactionDao;
import task.money.transfer.health.DatabaseHealthCheck;
import task.money.transfer.resources.AccountsResource;
import task.money.transfer.resources.CurrenciesResource;
import task.money.transfer.resources.MoneyResource;

public class MoneyApp extends Application<AppConfiguration> {

    public static void main(String... args) throws Exception {
        new MoneyApp().run(args);
    }

    @Override
    public String getName() {
        return "MoneyApp";
    }

    @Override
    public void run(AppConfiguration config, Environment env) {
        env.healthChecks().register("db-health", new DatabaseHealthCheck(config.getDataSourceFactory()));

        DBI dbi = new DBIFactory().build(env, config.getDataSourceFactory(), "h2");

        AccountDao accountDao = dbi.onDemand(AccountDao.class);
        TransactionDao transactionDao = dbi.onDemand(TransactionDao.class);
        CurrencyDao currencyDao = dbi.onDemand(CurrencyDao.class);

        accountDao.createTableIfNotExists();
        transactionDao.createTableIfNotExists();
        currencyDao.createTableIfNotExists();

        if (currencyDao.getAllSupportedCurrencies().isEmpty()) {
            new CurrenciesInitializer(currencyDao).populateCurrencies();
        }

        env.jersey().register(new AccountsResource(accountDao, currencyDao));
        env.jersey().register(new MoneyResource(accountDao, transactionDao));
        env.jersey().register(new CurrenciesResource(currencyDao));
    }

}
