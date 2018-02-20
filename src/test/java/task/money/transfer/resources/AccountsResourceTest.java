package task.money.transfer.resources;

import javax.annotation.ParametersAreNonnullByDefault;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task.money.transfer.api.Account;
import task.money.transfer.api.ApiResponse;
import task.money.transfer.api.ErrorApiResponse;
import task.money.transfer.api.OkApiResponse;
import task.money.transfer.api.OpenAccountRequest;
import task.money.transfer.api.err.ErrorCodes;
import task.money.transfer.db.AccountDao;
import task.money.transfer.db.CurrencyDao;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class AccountsResourceTest {

    @Mock
    private CurrencyDao currencies;

    @Mock
    private AccountDao accounts;

    @InjectMocks
    private AccountsResource resource;

    private ApiResponse result;

    @BeforeClass
    public void init() {
        initMocks(this);
    }

    @BeforeMethod
    public void reset() {
        Mockito.reset(currencies, accounts);
    }

    @Test
    public void getById_accountNull_Fail() {
        long accountId = 1L;
        when(accounts.findById(eq(accountId))).thenReturn(null);
        getById(accountId);

        expectError(ErrorCodes.OBJECT_NOT_FOUND);
    }

    @Test
    public void getById_accountFound_Ok() {
        long accountId = 3L;
        Account found = mock(Account.class);
        when(accounts.findById(eq(accountId))).thenReturn(found);
        getById(accountId);

        expectOkResponseWithBody(found);
    }

    @Test
    public void open_unsupportedCurrency_Fail() {
        int currency = 2020;
        when(currencies.isSupported(eq(currency))).thenReturn(false);
        open(currency);

        expectError(ErrorCodes.OBJECT_NOT_FOUND);
    }

    @Test
    public void open_currencySupported_Ok() {
        int currency = 30;
        when(currencies.isSupported(eq(currency))).thenReturn(true);
        open(currency);

        expectOkResponse();
    }

    private void getById(long id) {
        result = resource.getById(id);
    }

    private void open(int currency) {
        result = resource.open(new OpenAccountRequest(currency));
    }

    private void expectError(int errorCode) {
        assertTrue(result instanceof ErrorApiResponse);
        assertEquals(((ErrorApiResponse) result).getErrorCode(), errorCode);
    }

    private void expectOkResponse() {
        assertEquals(result.getStatus(), ApiResponse.Status.OK);
    }

    private void expectOkResponseWithBody(Object expectedBody) {
        expectOkResponse();
        assertTrue(result instanceof OkApiResponse);
        assertEquals(((OkApiResponse) result).getBody(), expectedBody);
    }


}
