package task.money.transfer.db;

import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.DBI;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task.money.transfer.api.Account;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ParametersAreNonnullByDefault
public class AccountDaoTest {

    private static AccountDao dao;

    @BeforeClass
    public static void setup() {
        DBI dbi = TestDbInitializer.getDbi();
        dao = dbi.onDemand(AccountDao.class);

        dbi.registerMapper(new AccountMapper());
    }

    @BeforeMethod
    public void reset() throws Exception {
        dao.dropAccountsTableSafely();
        dao.createAccountsTable();
    }

    @Test
    public void insertAndFindById() throws SQLException {
        final int currency = 840;
        final Account.Status status = Account.Status.ACTIVE;
        long id = dao.insert(currency, status.name()).longValue();
        Account account = dao.findById(id);

        assertNotNull(account);
        assertEquals(account.getId(), id);  // mapping is tested separately
    }

}
