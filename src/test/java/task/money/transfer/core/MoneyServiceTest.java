package task.money.transfer.core;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task.money.transfer.api.Account;
import task.money.transfer.api.err.ErrorCodes;
import task.money.transfer.api.resp.ApiResponse;
import task.money.transfer.api.resp.ErrorApiResponse;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.TransactionDao;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class MoneyServiceTest {

    private static final int CURR_1 = 1;
    private static final int CURR_2 = 2;

    @Mock
    private AccountDao accounts;

    @Mock
    private TransactionDao transactions;

    @InjectMocks
    private MoneyService service;

    private final long accountIdOne = 222;
    private final long accountIdTwo = 555;
    private final long amount = 60_000_000;

    private ApiResponse result;

    @BeforeClass
    public void init() {
        initMocks(this);
    }

    @BeforeMethod
    public void reset() {
        Mockito.reset(accounts, transactions);

        result = null;

        when(transactions.add(anyLong(), anyLong(), anyLong())).thenReturn(9090L);

        // default values: valid
        letAccountDaoReturn(accountIdOne, new Account(accountIdOne, CURR_1, Account.Status.ACTIVE));
        letAccountDaoReturn(accountIdTwo, new Account(accountIdTwo, CURR_1, Account.Status.ACTIVE));
        setBalance(accountIdOne, amount);
        setBalance(accountIdTwo, 0);
    }

    @Test
    public void withdraw_accountNotFound() throws Exception {
        letAccountDaoReturn(accountIdOne, null);
        result = service.withdraw(accountIdOne, amount);

        expectError(ErrorCodes.OBJECT_NOT_FOUND);
    }

    @Test
    public void withdraw_accountClosed() throws Exception {
        letAccountDaoReturn(accountIdOne, new Account(accountIdOne, CURR_1, Account.Status.CLOSED));
        result = service.withdraw(accountIdOne, amount);

        expectError(ErrorCodes.INCONSISTENT_STATE);
    }

    @Test
    public void withdraw_lowBalance() throws Exception {
        setBalance(accountIdOne, amount - 1);
        result = service.withdraw(accountIdOne, amount);

        expectError(ErrorCodes.INSUFFICIENT_FUNDS);
    }

    @Test
    public void withdraw_invalidAmount() throws Exception {
        result = service.withdraw(accountIdOne, amount - 3);

        expectError(ErrorCodes.INVALID_USE_OF_FIELD);
    }

    @Test
    public void withdraw_ok() throws Exception {
        result = service.withdraw(accountIdOne, amount);

        expectSuccess();
    }

    @Test
    public void transfer_senderNotFound() throws Exception {
        letAccountDaoReturn(accountIdOne, null);
        transfer(amount);

        expectError(ErrorCodes.OBJECT_NOT_FOUND);
    }

    @Test
    public void transfer_recipientNotFound() throws Exception {
        letAccountDaoReturn(accountIdTwo, null);
        transfer(amount);

        expectError(ErrorCodes.OBJECT_NOT_FOUND);
    }

    @Test
    public void transfer_senderClosed() throws Exception {
        letAccountDaoReturn(accountIdOne, new Account(accountIdOne, CURR_1, Account.Status.CLOSED));
        transfer(amount);

        expectError(ErrorCodes.INCONSISTENT_STATE);
    }

    @Test
    public void transfer_recipientClosed() throws Exception {
        letAccountDaoReturn(accountIdTwo, new Account(accountIdTwo, CURR_1, Account.Status.CLOSED));
        transfer(amount);

        expectError(ErrorCodes.INCONSISTENT_STATE);
    }

    @Test
    public void transfer_differentCurrencies() throws Exception {
        letAccountDaoReturn(accountIdTwo, new Account(accountIdTwo, CURR_2, Account.Status.ACTIVE));
        transfer(amount);

        expectError(ErrorCodes.INCONSISTENT_STATE);
    }

    @Test
    public void transfer_senderHasInsufficientFunds() throws Exception {
        setBalance(accountIdOne, amount - 1);
        transfer(amount);

        expectError(ErrorCodes.INSUFFICIENT_FUNDS);
    }

    @Test
    public void transfer_invalidAmount() throws Exception {
        transfer(amount - 117);

        expectError(ErrorCodes.INVALID_USE_OF_FIELD);
    }

    @Test
    public void transfer_ok() {
        transfer(amount);

        expectSuccess();
    }

    private void expectError(int errorCode) {
        assertTrue(result instanceof ErrorApiResponse);
        assertEquals(((ErrorApiResponse) result).getErrorCode(), errorCode);
    }

    private void expectSuccess() {
        assertEquals(result.getStatus(), ApiResponse.Status.OK);
    }

    private void transfer(long amount) {
        result = service.transfer(accountIdOne, accountIdTwo, amount);
    }

    private void letAccountDaoReturn(long accountId, @Nullable Account account) {
        when(accounts.findById(eq(accountId))).thenReturn(account);
    }

    private void setBalance(long accountId, long balance) {
        when(transactions.getBalance(eq(accountId))).thenReturn(balance);
    }

}
