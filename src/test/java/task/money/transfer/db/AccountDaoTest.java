package task.money.transfer.db;

import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.DBI;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task.money.transfer.api.Account;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@ParametersAreNonnullByDefault
public class AccountDaoTest {

    private static final int USD = 840;

    private AccountDao dao;

    @BeforeClass
    public void setup() {
        DBI dbi = TestDbInitializer.getDbi();
        dao = dbi.onDemand(AccountDao.class);
    }

    @Test
    public void insertAndFindById() throws SQLException {
        long id = dao.createAccount(USD);
        Account account = dao.findById(id);

        assertNotNull(account);
        assertEquals(account.getId(), id);  // mapping is tested separately
    }

    @Test
    public void testAutoIncrementId() throws Exception {
        long first = dao.createAccount(USD);
        long second = dao.createAccount(USD);

        assertNotEquals(first, second);
    }

    @Test
    public void tryGetNonExistentAccount() throws Exception {
        assertNull(dao.findById(787L));
    }

}
