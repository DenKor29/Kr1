

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DBServer implements DBConnectionListener {

    public static final String SQLDATATIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

    private Connection connection;
    private DBServerListener eventListener;
    private String nameDB;
    private String nameTable="route";
    private String nameMainTable="schedule";
    private boolean status;


    public DBServer(DBServerListener event,String url, String urlParam,String nameDB,String user, String password) {

        this.nameDB = nameDB;
        this.status = false;
        this.eventListener = event;




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

    private synchronized void sendQuery( String query, boolean update) {

        try {
            new DBConnection(this, connection, query,!update);
        } catch (SQLException e) {
            System.out.println("DBServer Query Exeption: " + e);
        }

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
        int Route = 1;


        String query = "INSERT INTO " + nameMainTable +" (Route,CountBilets,BeginTime)  \n" +
                "VALUES ('"+Route+"',\n" +
                "'" + baseData.CountBilets +"'," +
                "'" + baseData.BeginTime +"');";

        sendQuery(query,true);
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
    @Override
    public synchronized void onResultSet(DBConnection dbConnection, ResultSet resultSet, Statement statement) {
        try {

            ArrayList <DBData> listDBData = new ArrayList<>();

            while (resultSet.next()) {

                if (resultSet.getString("CallingNumber").trim().equals("null")) continue;
                if (resultSet.getString("CalledNumber").trim().equals("null")) continue;

                DBData baseData = new DBData();

                baseData.cond_code = resultSet.getString("CondCode");
                baseData.code_dial = resultSet.getString("CodeDial");
                baseData.code_used = resultSet.getString("CodeUsed");
                baseData.in_trk_code = resultSet.getString("InTrkCode");
                baseData.acct_code = resultSet.getString("AcctCode");
                baseData.auth_code = resultSet.getString("AuthCode");
                baseData.frl = resultSet.getString("Frl");
                baseData.ixc_code = resultSet.getString("IxcCode");
                baseData.in_crt_id = resultSet.getString("InCrtId");
                baseData.out_crt_id = resultSet.getString("OutCrtId");
                baseData.feat_flag = resultSet.getString("FeatFlag");
                baseData.code_return = resultSet.getString("CodeReturn");
                baseData.line_feed = resultSet.getString("LineFeed");

                listDBData.add(baseData);

            };

            eventListener.onRecivedCDR(this, listDBData);


            resultSet.close(); resultSet = null;
            statement.close(); statement = null;
            }
            catch (SQLException e) {
            System.out.println("DBServer Exeption: " + e);
            };

    }

}
