package task.money.transfer.db.currency;

import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Maps;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task.money.transfer.api.Currency;
import task.money.transfer.db.TestDbInitializer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class CurrencyDaoTest {

    private CurrencyDao dao;

    @BeforeClass
    public void setup() {
        DBI dbi = TestDbInitializer.getDbi();
        dao = dbi.onDemand(CurrencyDao.class);
    }

    @Test
    public void addAndCheckSupported() {
        dao.put(111, "AAA");

        assertTrue(dao.isSupported(111));
    }

    @Test(expectedExceptions = UnableToExecuteStatementException.class)
    public void cannotPutLongStringAsIso() {
        dao.put(222, "ABRA");
    }

    @Test
    public void checkUnsupported() {
        assertFalse(dao.isSupported(333));
    }

    @Test
    public void addTwoCheckList() {
        dao.put(3, "EEA");
        dao.put(4, "EEB");

        Map<Integer, Currency> index = Maps.uniqueIndex(dao.getAllSupportedCurrencies(), Currency::getNumCode);

        assertEquals(index.get(3).getIsoCode(), "EEA");
        assertEquals(index.get(4).getIsoCode(), "EEB");
    }

}
