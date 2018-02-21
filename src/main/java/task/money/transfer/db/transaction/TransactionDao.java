package task.money.transfer.db.transaction;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import task.money.transfer.api.Transaction;

@ParametersAreNonnullByDefault
public interface TransactionDao {

    @SqlUpdate(SQL.CREATE_TABLE_IF_NOT_EXISTS)
    void createTableIfNotExists();

    @SqlQuery(SQL.GET_ALL_BY_ACC_ID)
    @Mapper(TransactionMapper.class)
    List<Transaction> getHistory(@Bind("accountid") long accountId);   // TODO pagination? limit/offset

    @SqlQuery(SQL.GET_BY_ID)
    @Mapper(TransactionMapper.class)
    Transaction getById(@Bind(FieldNames.ID) long id);

    @SqlUpdate(SQL.INSERT)
    @GetGeneratedKeys
    long add(@Bind(FieldNames.SENDER) @Nullable Long sender,
            @Bind(FieldNames.RECIPIENT) @Nullable Long recipient,
            @Bind(FieldNames.AMOUNT) long amount);

    @SqlQuery(SQL.GET_BALANCE)
    long getBalance(@Bind("accountid") long accountId);

    class FieldNames {
        private FieldNames() {
        }

        static final String ID = "id";
        static final String SENDER = "sender";
        static final String RECIPIENT = "recipient";
        static final String AMOUNT = "amount";
        static final String HAPPENED = "happened";
    }

    class SQL {
        private SQL() {
        }

        static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists transactions"
                + " (id bigint primary key auto_increment,"
                + " sender bigint default null, foreign key (sender) references accounts(id),"
                + " recipient bigint default null, foreign key (recipient) references accounts(id),"
                + " amount bigint not null, check amount > 0,"
                + " happened timestamp not null default now());"
                + " create index if not exists trx_snd_id on transactions(sender);"
                + " create index if not exists trx_rcp_id on transactions(recipient);"
                + " create index if not exists trx_tmstmp on transactions(happened);";
        static final String DROP_TABLE_IF_EXISTS = "drop table transactions if exists;";
        static final String INSERT = "insert into transactions"
                + " (id, sender, recipient, amount, happened) values"
                + " (default, :sender, :recipient, :amount, default);";
        static final String GET_ALL_BY_ACC_ID = "select sender, recipient, amount, happened"
                + " from transactions where sender = :accountid or recipient = :accountid"
                + " order by happened desc;";
        static final String GET_BALANCE = "select"
                + " sum(casewhen(recipient = :accountid, amount, 0))"
                + " - sum(casewhen(sender = :accountid, amount, 0))"
                + " from transactions";
        static final String GET_BY_ID = "select id, sender, recipient, amount, happened"
                + " from transactions where id = :id;";
    }
}
