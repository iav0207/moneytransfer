package task.money.transfer.db;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import task.money.transfer.api.Account;

@ParametersAreNonnullByDefault
public interface AccountDao {

    class FieldNames {
        private FieldNames() {
        }

        static final String ID = "id";
        static final String CURRENCY = "currency";
        static final String STATUS = "status";
    }

    class SQL {
        private SQL() {
        }

        static final String CREATE_TABLE = "create table if not exists accounts"
                + " (id bigint primary key auto_increment,"
                + " currency int not null, foreign key (currency) references currencies(code),"
                + " status varchar(30) default 'ACTIVE'"
                + " check status in ('ACTIVE', 'SUSPENDED', 'CLOSED'));"
                + " create index if not exists acc_curr on accounts(currency);";
        static final String DROP_TABLE = "drop table accounts if exists";
        static final String INSERT = "insert into accounts (id, currency, status) values (default, :currency, default)";
        static final String FIND_BY_ID = "select id, currency, status from accounts where id = :id";
    }

    @SqlUpdate(SQL.CREATE_TABLE)
    void createTableIfNotExists();

    @SqlUpdate(SQL.DROP_TABLE)
    void dropAccountsTableSafely();

    @SqlUpdate(SQL.INSERT)
    @GetGeneratedKeys
    long createAccount(@Bind(FieldNames.CURRENCY) int currency);

    @SqlQuery(SQL.FIND_BY_ID)
    @Mapper(AccountMapper.class)
    Account findById(@Bind(FieldNames.ID) long id);
}
