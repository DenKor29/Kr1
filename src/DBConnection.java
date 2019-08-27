
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private final Connection connection;
    private final DBConnectionListener eventListener;
    private final String sql ;

    public ResultSet getResultSet() {
        return resultSet;
    }

    private ResultSet resultSet;

    public Statement getStatement() {
        return statement;
    }

    private Statement statement;


    public DBConnection(DBConnectionListener event, Connection conn, String query, boolean result) throws SQLException {
        this.eventListener = event;
        this.connection = conn;
        this.sql = query;
        System.out.println("Query = " + query );

                try {

                    statement = connection.createStatement();
                     if (result) {
                         resultSet  = statement.executeQuery(sql);

                     } else  {
                         resultSet = null;
                         statement.executeUpdate(sql);
                     }

                } catch (SQLException se) {
                    eventListener.onException(DBConnection.this,se);
                }

    }
    @Override
    public String toString() {
        return "DBConnection: " + sql;
    }
}
