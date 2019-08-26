package kr1.server;

import avayacdr.database.DBServer;
import avayacdr.database.DBServerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class MainWindow implements DBServerListener  {
    public static void main(String[] args) {
         new MainWindow();
    });
    }

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
    public synchronized void onRecivedCDR(DBServer dbServer, HTTPRequest httpRequest,ArrayList <AvayaCDRData> cdrData) {
            cdrHttpServer.SendResponseConnection(httpRequest,cdrData);

    }

    @Override
    public void onFindDBDateZapros(HTTPRequest httpRequest, LocalDateTime BeginTime, LocalDateTime EndTime, String Key, String Value, int opKey) {
     dbServer.FindDateTimeTable(httpRequest,BeginTime,EndTime,Key,Value,opKey);
    }



}
