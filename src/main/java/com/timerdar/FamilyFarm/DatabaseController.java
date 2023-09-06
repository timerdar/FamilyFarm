package com.timerdar.FamilyFarm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {

    public static final String db_url = System.getenv("db_url") + System.getenv("db_name");
    public static final String db_user = System.getenv("db_user");
    public static final String db_password = System.getenv("db_password");

    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(db_url, db_user, db_password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}