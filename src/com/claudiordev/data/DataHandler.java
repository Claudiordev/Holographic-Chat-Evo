package com.claudiordev.data;

import com.claudiordev.config.Configuration;
import com.claudiordev.main.Main;

import java.sql.SQLException;

/**
 * Handles the correct Method to each type of Connection chosen on the config.yml file;
 */
public class DataHandler {

    static SQLLiteData sqlLiteData = new SQLLiteData();
    static MySQLData mySQLData = new MySQLData();

    public static void handleInsert(String uniqueid, Integer state) {

        switch (Configuration.getDataType()) {
            case 1:
                sqlLiteData.insertData(uniqueid,state);
                break;
            case 2:
                mySQLData.insertData(uniqueid,state);
                break;
        }

        Main.getPlugin().getLogger().info("New player detected, adding to the Database");
    }

    public static Object retrieveData(String query, String type) throws Exception {

        switch (Configuration.getDataType()) {
            case 1:
                return sqlLiteData.retrieveData(query,type);
            case 2:
                return mySQLData.retrieveData(query,type);
        }

        return null;
    }

    public static void manageData(String query) throws SQLException {
        switch (Configuration.getDataType()) {
            case 1:
                sqlLiteData.manageData(query);
                break;
            case 2:
                mySQLData.manageData(query);
                break;
        }
    }
}
