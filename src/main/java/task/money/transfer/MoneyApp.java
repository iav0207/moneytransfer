package task.money.transfer;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.AccountMapper;
import task.money.transfer.health.DatabaseHealthCheck;
import task.money.transfer.resources.AccountsResource;

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

        DBI jdbi = new DBIFactory().build(env, config.getDataSourceFactory(), "h2");

        AccountDao dao = jdbi.onDemand(AccountDao.class);
        jdbi.registerMapper(new AccountMapper());

        dao.createAccountsTable();  // TODO handle persistent db case

        env.jersey().register(new AccountsResource(dao));
    }

}
