package com.claudiordev.data;

import com.claudiordev.main.Main;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLLiteData extends AbstractData{

    //This File object is only respective to the Directory, not any file it self;
    static File file = new File(Main.getPlugin().getDataFolder().toString() + "\\db");

    //"org.sqlite.JDBC"

    public SQLLiteData() {

    }

    public SQLLiteData(String url) {
        this.url = url;
        createdir();
    }

    /**
     * Create Directory for the SQLLite Database;
     */
    public void createdir() {
        if (!file.exists()) {
            file.mkdir();
            Main.getPlugin().getLogger().info("Chega aqui(2)");
        }
    }

    /**
     * Delete Directory for the SQLLite Database;
     */
    public void deletedir() {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().info("Error: " + e);
        }
    }

    /**
     * Create a table within the Database;
     */
    public void createTable(String tableName) {
        classCall();

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	uniqueid TEXT,\n"
                + "	state INTEGER DEFAULT 1\n"
                + ");";

        this.tablename = tableName;
        String local_url = "jdbc:sqlite:plugins/HolographicChatEvo/db/Data.db";

        try (Connection conn = DriverManager.getConnection(local_url);
             Statement stmt = conn.createStatement()) {
            //Execute statement
            stmt.execute(sql);
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }

    /**
     * @param query String query used to select the data;
     * @return A resultset of data with a Select Query
     */
    public ResultSet retrieveAllData(String query) {
        try {
            classCall();

            String local_url = "jdbc:sqlite:plugins/HolographicChatEvo/db/Data.db";

            Connection conn = DriverManager.getConnection(local_url);
            Statement statement = conn.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  null;
    }

    public void insertImport(String uniqueid, Integer state) {
        classCall();

        String query = "INSERT INTO data (uniqueid,state) \n"
                + "VALUES('" +uniqueid + "', "+state+");";

        String local_url = "jdbc:sqlite:plugins/HolographicChatEvo/db/Data.db";

        try (Connection conn = DriverManager.getConnection(local_url)) {
            Statement stmt = conn.createStatement();
            stmt.execute(query);

            Main.getPlugin().getLogger().info("Data inserted with success;");

            //Closes conn automatically after Try/Catch;
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }

    void classCall() {
        //Set the Driver class for JDBC MySQL
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
