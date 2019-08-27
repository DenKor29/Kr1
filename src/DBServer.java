

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DBServer implements DBConnectionListener {

    public static final String SQLDATATIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

    private Connection connection;
    private DBServerListener eventListener;
    private String nameDB="station";
    private String nameTable="route";
    private String nameMainTable="schedule";
    private boolean status;


    public DBServer(DBServerListener event) {

        this.nameDB = nameDB;
        this.status = false;
        this.eventListener = event;

        String user = "root";
        String password = "15122000";
        String url = "jdbc:mysql://127.0.0.1:3306/";
        String urlParam = "?serverTimezone=GMT%2B3";


        try {
            connection = DriverManager.getConnection(url + nameDB + urlParam, user, password);
            System.out.println("DBServer Start..." );
            if (isExistTable(connection,nameTable)) {
                System.out.println("DBServer Table:"+nameTable+" is exist." );
            } else CreateTable();
            if (isExistTable(connection,nameMainTable)) {
                System.out.println("DBServer Table:"+nameMainTable+" is exist." );
            } else CreateTable();
        } catch (SQLException e) {
            System.out.println("DBServer Fault Start..." );
            System.out.println("DBServer Exeption: " + e);
        }

        status = true;
    }

    protected final LocalDateTime GetFieldTime(String value)
    {
        LocalDateTime dateTime = LocalDateTime.now();
        try{
             dateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(SQLDATATIMEFORMAT));
        }
        catch(DateTimeParseException e){
            System.out.println("DateTime Exeption: " + e);
        }
        return dateTime;
    }

    private boolean isExistTable(Connection connection, String name){

        boolean isExist = false;
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, name, null);
            if (rs.next())  isExist =true;
        } catch (SQLException e) {
            System.out.println("DBServer Exeption: " + e);
        }
        return isExist;
    }

    private synchronized DBConnection sendQuery( String query, boolean update) {

        try {
            return new DBConnection(this, connection, query,!update);

        } catch (SQLException e) {
            System.out.println("DBServer Query Exeption: " + e);
        }
        return null;
    }



    public void CreateMainTable(){

        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;

        System.out.println("DBServer Create Table " + nameMainTable + " ...");
        String query = "CREATE TABLE IF NOT EXISTS "+ nameMainTable + " (\n" +
                "    Id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "    Route int(11), \n" +
                "    CountBilets int(11), \n" +
                "    BeginTime datatime,\n" +
                "    PRIMARY KEY (Id));";
        sendQuery(query,true);


    }

    public void CreateTable(){

        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;

        System.out.println("DBServer Create Table " + nameTable + " ...");
        String query = "CREATE TABLE IF NOT EXISTS "+ nameTable + " (\n" +
                "    Id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "    PunktA varchar(255),\n" +
                "    PunktB varchar(255), \n" +
                "    L int(11), \n" +
                "    TimeL int(11), \n" +
                "    Automobile varchar(255), \n" +
                "    Count int(11), \n" +
                "    PRIMARY KEY (Id));";
        sendQuery(query,true);


    }

    private String LocalDateTimeToString(LocalDateTime localDateTime){

        String result = "";
        LocalDateTime dateTime;
        if (localDateTime == null) dateTime =LocalDateTime.now(); else dateTime = localDateTime;

        try {
            result = dateTime.format(DateTimeFormatter.ofPattern(SQLDATATIMEFORMAT));
        } catch (DateTimeParseException e) {
            System.out.println("DBServer SQLDateTime Exeption: " + e);
        }

        return  result;
    }

    public void AppendMainTableString(DBData baseData)
    {
        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;

        String BeginTime = LocalDateTimeToString(baseData.BeginTime);


        String query = "INSERT INTO " + nameMainTable +" (Route,CountBilets,BeginTime)  \n" +
                "VALUES ('"+baseData.Route+"',\n" +
                "'" + baseData.CountBilets +"'," +
                "'" + baseData.BeginTime +"');";

        sendQuery(query,true);
    }

    public void ShowMainTableString()
    {
        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;

        String query = "SELECT schedule.Id,schedule.Route,schedule.BeginTime,schedule.CountBilets,route.Id,route.PunktA,route.PunktB,route.L,route.TimeL,route.Automobile,route.Count FROM schedule,route "+
                " WHERE (schedule.Route = route.Id)";

        DBConnection connection = sendQuery(query,false);

        if (connection == null) return;


        try {

            ResultSet resultSet = connection.getResultSet();
             while (resultSet.next()) {

                System.out.println("\n======================================================================");

                 System.out.println("Id: " + Util.GetIntFromString(resultSet.getString("schedule.Id"),0));
                 System.out.println("Время: " + GetFieldTime(resultSet.getString("schedule.BeginTime")));
                System.out.println("Automobile: " + resultSet.getString("Route.Automobile"));
                System.out.println("PunktA: " + resultSet.getString("Route.PunktA"));
                System.out.println("PunktB: " + resultSet.getString("Route.PunktB"));
                System.out.println("L: " + Util.GetIntFromString(resultSet.getString("Route.L"),0));
                System.out.println("TimeL: " + Util.GetIntFromString(resultSet.getString("Route.TimeL"),0));
                System.out.println("CountBilets: " + Util.GetIntFromString(resultSet.getString("schedule.CountBilets"),0));
                System.out.println("Count: " + Util.GetIntFromString(resultSet.getString("Route.Count"),0));
                System.out.println("======================================================================");


            }
            ;
        }
        catch (SQLException e)
        {

            System.out.println("DBServer Exeption: " + e);
        };

        return ;

    }


    public void AppendTableString(DBData baseData)
    {
        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;

        //String BeginDate = LocalDateTimeToString(baseData.BeginTime);



        String query = "INSERT INTO " + nameTable +" (PunktA,PunktB,L,TimeL,Automobile,Count)  \n" +
                "VALUES ('"+baseData.PunktA+"',\n" +
                "'" + baseData.PunktB +"'," +
                "'" + baseData.L +"'," +
                "'" + baseData.TimeL +"'," +
                "'" + baseData.Automobile +"'," +
                "'" + baseData.Count +"');";

        sendQuery(query,true);
    }

    public void FindDateTimeTable(LocalDateTime BeginTime, LocalDateTime EndTime, String Key, String Value,int opKey)
    {
        //Не запускаем общие методы без полной инициализации класса
        if (!status) return;


        String BeginDate = LocalDateTimeToString(BeginTime);
        String EndDate = LocalDateTimeToString(EndTime);
        String operandBegin = "";
        String operandEnd = "";


        switch (opKey){
            case 0: {operandBegin = "LIKE"; operandEnd = "%"; break;}
            case 1: {operandBegin = ">";break;}
            case 2: {operandBegin = "<";break;}
            case 3: {operandBegin = ">=";break;}
            case 4: {operandBegin = "<=";break;}
        };

        String locValue = Value;
        if (Key.equals("Duration"))
        {
            int val = Util.GetIntFromString(Value)/6;
            locValue = ""+ val;
        }

        String query = "SELECT Value,Date,Duration,CondCode,CodeDial,CodeUsed,InTrkCode,CallingNumber,CalledNumber,AcctCode,AuthCode,Frl,IxcCode,InCrtId,OutCrtId,FeatFlag,CodeReturn,LineFeed FROM " + nameTable
                + " WHERE (Date BETWEEN '" +             BeginDate +"' AND '" + EndDate +"')"
                + " AND (" + Key +" "+operandBegin+" '" + locValue +operandEnd+"')";

        sendQuery(query,false);

    }

    public DBConnection FindChildRouteTable(DBData dbData)
    {
        //Не запускаем общие методы без полной инициализации класса
        if (!status) return null;


        String query = "SELECT Id,PunktA,PunktB,L,TimeL,Automobile,Count FROM " + nameTable
                + " WHERE (PunktA = '" +  dbData.PunktA +"')"
                + " AND   (PunktB = '" +  dbData.PunktB +"')"
                + " AND   (L = '"      +  dbData.L +"')"
                + " AND   (TimeL = '"  +  dbData.TimeL +"')"
                + " AND   (Automobile = '" +  dbData.Automobile +"')"
                + " AND   (Count = '"  +  dbData.Count +"')";


        return sendQuery(query,false);

    }

    @Override
    public synchronized void onConnectionReady(DBConnection dbConnection) {
       System.out.println("DBConnection  Ready.");

    }

    @Override
    public synchronized void onDisconnection(DBConnection dbConnection) {
        System.out.println("DBConnection  Disconnect.");
    }

    @Override
    public synchronized void onException(DBConnection dbConnection, Exception e) {
        System.out.println("DBServer Exeption: " + e);

    }

    public synchronized void onResultSet(DBConnection dbConnection, ResultSet resultSet, Statement statement) {
        try {

            ArrayList <DBData> listDBData = new ArrayList<>();

            while (resultSet.next()) {

                if (resultSet.getString("Id").trim().equals("null")) continue;

                DBData baseData = new DBData();

                baseData.BeginTime = GetFieldTime(resultSet.getString("BeginTime"));
                baseData.Automobile = resultSet.getString("Automobile");
                baseData.CountBilets = Util.GetIntFromString(resultSet.getString("CountBilets"),0);
                baseData.L = Util.GetIntFromString(resultSet.getString("L"),0);
                baseData.TimeL = Util.GetIntFromString(resultSet.getString("TimeL"),0);
                baseData.PunktA = resultSet.getString("PunktA");
                baseData.PunktB = resultSet.getString("PunktB");
                baseData.Count = Util.GetIntFromString(resultSet.getString("Count"),0);

                listDBData.add(baseData);

            };




            resultSet.close(); resultSet = null;
            statement.close(); statement = null;
            }
            catch (SQLException e) {
            System.out.println("DBServer Exeption: " + e);
            };

    }

}
