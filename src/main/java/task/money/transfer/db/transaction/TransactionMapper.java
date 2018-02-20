package task.money.transfer.db.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.money.transfer.api.Transaction;

@ParametersAreNonnullByDefault
public class TransactionMapper implements ResultSetMapper<Transaction> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMapper.class);

    @Override
    public Transaction map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Long sender = resultSet.getLong(TransactionDao.FieldNames.SENDER);
        Long recipient = resultSet.getLong(TransactionDao.FieldNames.RECIPIENT);
        Long amount = resultSet.getLong(TransactionDao.FieldNames.AMOUNT);
        DateTime happened = new DateTime(resultSet.getTimestamp(TransactionDao.FieldNames.HAPPENED));
        logger.debug("ResultSet: sender={} recipient={} amount={} happened={}",
                sender, recipient, amount, happened);
        sender = sender == 0 ? null : sender;
        recipient = recipient == 0 ? null : recipient;
        return new Transaction(sender, recipient, amount, happened);
    }
}
