package org.example.database;

import org.example.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection(){

        try{
            if(connection == null || connection.isClosed()){

                Class.forName(ConfigLoader.get("db.driver"));

                connection = DriverManager.getConnection(
                        ConfigLoader.get("db.url"),
                        ConfigLoader.get("db.username"),
                        ConfigLoader.get("db.password")
                );
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return connection;
    }
}
