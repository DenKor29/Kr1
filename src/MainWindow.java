
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class MainWindow implements DBServerListener  {

    public static void main(String[] args) {
         new MainWindow();
    };

    private DBServer dbServer;


    private MainWindow(){

        dbServer = new DBServer(this);
        DBData dbData = new DBData();
        dbData.Automobile="Маршрут 103.";
        dbData.PunktA="Городец";
        dbData.PunktB="Ясная Поляна";
        dbData.BeginTime=LocalDateTime.now();
        dbData.L=15;
        dbData.TimeL=120;
        dbData.CountBilets=0;
        dbData.Count=30;

        //Заполняем справочник маршрутов
        dbServer.AppendTableString(dbData);

        //Ищем запись в подчиненонй таблице
        DBConnection connection = dbServer.FindChildRouteTable(dbData);
        if (connection!=null) {
            //Ищем Id Route
            ResultSet resultSet = connection.getResultSet();

            if (resultSet != null)
            {
                try {
                    while (resultSet.next()) {

                        dbData.Route = Util.GetIntFromString(resultSet.getString("Id"), 0);
                        break;
                    }
                    ;
                }
                catch (SQLException e)
                {
                    dbData.Route = 0;
                    System.out.println("DBServer Exeption: " + e);
                };
            };

            //Запишем расписание маршрутов
            dbServer.AppendMainTableString(dbData);

            //Выведем расписание
            dbServer.ShowMainTableString();
        }





    }





    @Override
    public synchronized void onRecivedCDR(DBServer dbServer, ArrayList<DBData> cdrData) {


    }



}
