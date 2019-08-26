
import java.time.LocalDateTime;
import java.util.ArrayList;


public class MainWindow implements DBServerListener  {

    public static void main(String[] args) {
         new MainWindow();
    };

    private DBServer dbServer;


    private MainWindow(){


         String dbname = "station";
         String nametable = "schedule";
         String user = "user";
         String password = "12345";
         String url = "jdbc:mysql://127.0.0.1:3506/";
         String urlParam = "?serverTimezone=GMT%2B3";


        dbServer = new DBServer(this,url,urlParam,dbname,nametable,user,password);



    }





    @Override
    public synchronized void onRecivedCDR(DBServer dbServer, ArrayList<DBData> cdrData) {


    }



}
