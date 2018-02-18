package task.money.transfer.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.money.transfer.api.Account;

@ParametersAreNonnullByDefault
public class AccountMapper implements ResultSetMapper<Account> {

    private static final Logger logger = LoggerFactory.getLogger(AccountMapper.class);

    @Override
    public Account map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Long id = resultSet.getLong(AccountDao.FieldNames.ID);
        Integer curr = resultSet.getInt(AccountDao.FieldNames.CURRENCY);
        String status = resultSet.getString(AccountDao.FieldNames.STATUS);
        logger.debug("ResultSet: id={} cur={} status={}", id, curr, status);
        return new Account(id, curr, Account.Status.fromString(status));
    }
}
