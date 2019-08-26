
import java.util.ArrayList;

public interface DBServerListener {
    void onRecivedCDR(DBServer dbServer,  ArrayList <DBData> cdrData);
}
