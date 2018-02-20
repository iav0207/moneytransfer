package task.money.transfer.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import task.money.transfer.api.Account;
import task.money.transfer.db.account.AccountDao;
import task.money.transfer.db.account.AccountMapper;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ParametersAreNonnullByDefault
public class AccountMapperTest {

    @Mock
    private ResultSet resultSet;

    private AccountMapper mapper;

    @BeforeTest
    public void setup() {
        initMocks(this);
        mapper = new AccountMapper();
    }

    @Test
    public void shouldMapAllFields() throws SQLException {
        final long id = 9L;
        final Integer currency = 643;
        final Account.Status status = Account.Status.SUSPENDED;

        when(resultSet.getLong(AccountDao.FieldNames.ID)).thenReturn(id);
        when(resultSet.getInt(AccountDao.FieldNames.CURRENCY)).thenReturn(currency);
        when(resultSet.getString(AccountDao.FieldNames.STATUS)).thenReturn(status.name());

        Account account = mapper.map(0, resultSet, null);

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(account.getId(), id);
        softly.assertEquals(account.getCurrencyCode(), currency);
        softly.assertEquals(account.getStatus(), status);
        softly.assertAll();
    }
}
