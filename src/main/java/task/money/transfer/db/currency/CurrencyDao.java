package task.money.transfer.db.currency;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import task.money.transfer.api.Currency;

/**
 * Access to currencies classifier table.
 * <p>
 * Currencies of different number of digits after decimal separator
 * are not supported for the sake of simplicity.
 */
@ParametersAreNonnullByDefault
public interface CurrencyDao {

    @SqlUpdate(SQL.CREATE_TABLE_IF_NOT_EXISTS)
    void createTableIfNotExists();

    @SqlUpdate(SQL.INSERT)
    void put(@Bind(FieldNames.CODE) int numCode, @Bind(FieldNames.ISO) String isoCode);

    @SqlQuery(SQL.CHECK_CODE_EXISTS)
    boolean isSupported(@Bind(FieldNames.CODE) int code);

    @SqlQuery(SQL.GET_ALL)
    @Mapper(CurrencyMapper.class)
    List<Currency> getAllSupportedCurrencies();

    class SQL {
        private SQL() {
        }

        static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists currencies"
                + " (code int primary key, iso varchar(3) not null unique);";
        static final String CHECK_CODE_EXISTS = "select count(*) > 0 from currencies where code = :code;";
        static final String INSERT = "insert into currencies (code, iso) values (:code, :iso);";
        static final String GET_ALL = "select code, iso from currencies order by code asc";
    }

    class FieldNames {
        private FieldNames() {
        }

        static final String CODE = "code";
        static final String ISO = "iso";
    }

}
