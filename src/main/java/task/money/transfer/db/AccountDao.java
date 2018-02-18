package task.money.transfer.db;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.sqlobject.Bind;
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

        static final String CREATE_TABLE = "create table accounts "
                + "(id bigint primary key auto_increment, currency int, status varchar(30))";
        static final String DROP_TABLE = "DROP TABLE accounts IF EXISTS";
        static final String INSERT = "insert into accounts (currency, status) values (:currency, :status)";
        static final String FIND_BY_ID = "select id, currency, status from accounts where id = :id";
    }

    @SqlUpdate(SQL.CREATE_TABLE)
    void createAccountsTable();

    @SqlUpdate(SQL.DROP_TABLE)
    void dropAccountsTableSafely();

    @SqlUpdate(SQL.INSERT)
    Number insert(@Bind(FieldNames.CURRENCY) int currency, @Bind(FieldNames.STATUS) String status);

    @SqlQuery(SQL.FIND_BY_ID)
    @Mapper(AccountMapper.class)
    Account findById(@Bind(FieldNames.ID) long id);
}
