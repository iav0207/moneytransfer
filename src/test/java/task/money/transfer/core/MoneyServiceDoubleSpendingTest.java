package task.money.transfer.core;

import java.util.concurrent.CyclicBarrier;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.RandomUtils;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.db.TestDbInitializer;
import task.money.transfer.db.account.AccountDao;
import task.money.transfer.db.transaction.TransactionDao;

import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class MoneyServiceDoubleSpendingTest {

    private static final Logger logger = LoggerFactory.getLogger(MoneyServiceDoubleSpendingTest.class);

    private static final int THREAD_COUNT = 10;
    private static final long INITIAL_BALANCE = 5_000_000;
    private static final long AMOUNT = 1_000_000;
    private static final int CURRENCY = 840;

    private AccountDao accounts;
    private TransactionDao transactions;
    private MoneyService service;

    private long sender, recipient;

    private CyclicBarrier cyclicBarrier = new CyclicBarrier(THREAD_COUNT);

    @BeforeTest
    public void reset() {
        DBI dbi = TestDbInitializer.getDbi();
        accounts = dbi.onDemand(AccountDao.class);
        transactions = dbi.onDemand(TransactionDao.class);
        service = new MoneyService(transactions, accounts);
        sender = accounts.createAccount(CURRENCY);
        recipient = accounts.createAccount(CURRENCY);
        service.deposit(sender, INITIAL_BALANCE);
    }

    @Test(threadPoolSize = THREAD_COUNT, invocationCount = THREAD_COUNT)
    public void shouldNeverGetNegativeBalance() throws Exception {
        boolean transfer = RandomUtils.nextBoolean();
        cyclicBarrier.await();

        ApiResponse response = transfer ?
                service.transfer(sender, recipient, AMOUNT) : service.withdraw(sender, AMOUNT);
        logger.info("Response status: " + response.getStatus().name());

        long balance = transactions.getBalance(sender);
        logger.info("Balance: " + balance);
        assertTrue(balance >= 0);
    }
}
