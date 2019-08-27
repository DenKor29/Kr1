
import java.sql.ResultSet;
import java.sql.Statement;

public interface DBConnectionListener {
    void onConnectionReady(DBConnection dbConnection);
    void onDisconnection(DBConnection dbConnection);
    void onException(DBConnection dbConnection, Exception e);

}
