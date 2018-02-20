package task.money.transfer.db.currency;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.money.transfer.api.Currency;

@ParametersAreNonnullByDefault
public class CurrencyMapper implements ResultSetMapper<Currency> {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyMapper.class);

    @Override
    public Currency map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Integer code = resultSet.getInt(CurrencyDao.FieldNames.CODE);
        String iso = resultSet.getString(CurrencyDao.FieldNames.ISO);
        logger.debug("ResultSet: code={} iso={}", code, iso);
        return new Currency(code, iso);
    }
}
