package cz.cvut.fel.integracniportal;

import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.common.jdbc.UnitOfWorkAwareConnectionProviderWrapper;
import org.axonframework.eventstore.EventStoreException;
import org.axonframework.eventstore.jdbc.DefaultEventEntryStore;
import org.axonframework.eventstore.jdbc.EventSqlSchema;
import org.axonframework.eventstore.jdbc.SchemaConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.axonframework.common.jdbc.JdbcUtils.closeQuietly;

/**
 * @author Radek Jezdik
 */
public class SchemaDroppingEventEntryStore<T> extends DefaultEventEntryStore<T> {

    private final ConnectionProvider connectionProvider;

    public SchemaDroppingEventEntryStore(DataSource dataSource, EventSqlSchema<T> sqlSchema) {
        this(new UnitOfWorkAwareConnectionProviderWrapper(new DataSourceConnectionProvider(dataSource)), sqlSchema);
    }

    public SchemaDroppingEventEntryStore(ConnectionProvider connectionProvider, EventSqlSchema<T> sqlSchema) {
        super(connectionProvider, sqlSchema);
        this.connectionProvider = connectionProvider;
    }

    public SchemaDroppingEventEntryStore(ConnectionProvider connectionProvider) {
        super(connectionProvider);
        this.connectionProvider = connectionProvider;
    }

    public void dropSchema() throws SQLException {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            executeUpdate(sql_createDomainEventEntryTable(connection), "create domain event entry table");
            executeUpdate(sql_createSnapshotEventEntryTable(connection), "create snapshot entry table");
        } finally {
            closeQuietly(connection);
        }
    }

    public PreparedStatement sql_createSnapshotEventEntryTable(Connection connection) throws SQLException {
        final String sql = "drop table " + new SchemaConfiguration().snapshotEntryTable() + ";\n";
        return connection.prepareStatement(sql);
    }

    public PreparedStatement sql_createDomainEventEntryTable(Connection connection) throws SQLException {
        final String sql = "drop table " + new SchemaConfiguration().domainEventEntryTable() + ";\n";
        return connection.prepareStatement(sql);
    }

    private int executeUpdate(PreparedStatement preparedStatement, String description) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new EventStoreException("Exception occurred while attempting to " + description, e);
        } finally {
            closeQuietly(preparedStatement);
        }
    }

}
