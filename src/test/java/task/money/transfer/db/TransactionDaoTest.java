package task.money.transfer.db;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task.money.transfer.api.Transaction;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class TransactionDaoTest {

    private static final int USD = 840;
    private static final long AMOUNT = 3_000_000;

    private AccountDao accounts;
    private TransactionDao transactions;

    private long accountOne;
    private long accountTwo;

    @BeforeClass
    public void setup() {
        DBI dbi = TestDbInitializer.getDbi();
        accounts = dbi.onDemand(AccountDao.class);
        transactions = dbi.onDemand(TransactionDao.class);
    }

    @BeforeMethod
    public void reset() {
        accountOne = accounts.createAccount(USD);
        accountTwo = accounts.createAccount(USD);
    }

    @Test
    public void newAccountShouldHaveEmptyHistory() throws Exception {
        long accountId = accounts.createAccount(USD);
        List<Transaction> history = transactions.getHistory(accountId);

        assertEquals(history.size(), 0);
    }

    @Test
    public void testAutoIncrement() throws Exception {
        long firstTransactionId = transactions.add(accountOne, accountTwo, AMOUNT);
        long secondTransactionId = transactions.add(accountOne, accountTwo, AMOUNT);

        assertNotEquals(firstTransactionId, secondTransactionId);
    }

    @Test
    public void twoTransfersInHistory() throws Exception {
        transactions.add(accountOne, accountTwo, AMOUNT);
        transactions.add(accountOne, accountTwo, AMOUNT);

        assertEquals(transactions.getHistory(accountOne).size(), 2);
        assertEquals(transactions.getHistory(accountTwo).size(), 2);
    }

    @Test
    public void checkHistoryIsChronologicallyOrderedBackwards() throws Exception {
        for (int i = 0; i < 10; i++) {
            transactions.add(accountOne, accountTwo, AMOUNT);
        }
        List<Transaction> history = transactions.getHistory(accountOne);
        DateTime prevTime = null;
        for (Transaction trx : history) {
            if (prevTime == null) {
                prevTime = trx.getHappened();
                continue;
            }
            assertTrue(trx.getHappened().isBefore(prevTime));
        }
    }

    @Test
    public void testBalance() throws Exception {
        transactions.add(null, accountOne, 10 * AMOUNT);

        assertEquals(transactions.getBalance(accountOne) , 10 * AMOUNT);
        assertEquals(transactions.getBalance(accountTwo) , 0);

        transactions.add(accountOne, accountTwo, 7 * AMOUNT);

        assertEquals(transactions.getBalance(accountOne), 3 * AMOUNT);
        assertEquals(transactions.getBalance(accountTwo), 7 * AMOUNT);

        transactions.add(accountTwo, accountOne, 20 * AMOUNT);

        assertEquals(transactions.getBalance(accountOne), 23 * AMOUNT);
        assertEquals(transactions.getBalance(accountTwo), -13 * AMOUNT);

        transactions.add(accountOne, null, AMOUNT);

        assertEquals(transactions.getBalance(accountOne), 22 * AMOUNT);
    }

}
