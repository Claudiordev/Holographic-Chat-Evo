package com.claudiordev.data;

import com.claudiordev.config.Configuration;
import com.claudiordev.main.Main;

import java.sql.*;

public class MySQLData extends AbstractData{

    //com.mysql.jdbc.Driver

    //Default Constructor
    public MySQLData() {

    }

    public MySQLData(String username, String password) {
        this.url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
        this.username = username;
        this.password = password;
    }

    /**
     * Create a table within the Database;
     */
    public void createTable(String tableName) {
        classCall();

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "	id INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
                + "	uniqueid TEXT,\n"
                + "	state INTEGER DEFAULT 1\n"
                + ");";

        this.tablename = tableName;
        String local_url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
        String local_username = Configuration.getMySQLUsername();
        String local_password = Configuration.getMySQLPassword();

        try (Connection conn = DriverManager.getConnection(local_url, local_username, local_password);
             Statement stmt = conn.createStatement()) {
            //Execute statement
            stmt.execute(sql);
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }

    public void dropTable(String tableName) {
        classCall();

        String sql = "DROP TABLE IF EXISTS " + tableName;

        String local_url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
        String local_username = Configuration.getMySQLUsername();
        String local_password = Configuration.getMySQLPassword();

        try (Connection conn = DriverManager.getConnection(local_url, local_username, local_password);
             Statement stmt = conn.createStatement()) {
            //Execute statement
            stmt.execute(sql);
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }

    /**
     * @return True if the connection was made with success, and false if not
     */
    public boolean tryConnection() {
        classCall();

        String local_url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
        String local_username = Configuration.getMySQLUsername();
        String local_password = Configuration.getMySQLPassword();

        try (Connection conn = DriverManager.getConnection(local_url, local_username, local_password)) {
            Main.getPlugin().getLogger().info("Connected with success do MySQL Database");
            conn.close();
            return true;

        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
        return false;
    }

    public void insertImport(String uniqueid, Integer state) {
        classCall();

        String query = "INSERT INTO data (uniqueid,state) \n"
                + "VALUES('" +uniqueid + "', "+state+");";

        String local_url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
        String local_username = Configuration.getMySQLUsername();
        String local_password = Configuration.getMySQLPassword();

        try (Connection conn = DriverManager.getConnection(local_url, local_username, local_password)) {
             Statement stmt = conn.createStatement();
             stmt.execute(query);

            Main.getPlugin().getLogger().info("Data inserted with success;");

            //Closes conn automatically after Try/Catch;
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }

    /**
     * @param query String query used to select the data;
     * @return A resultset of data with a Select Query
     */
    public ResultSet retrieveAllData(String query, String class_name) {
        try {
            Class.forName(class_name);

            String local_url = "jdbc:mysql://" + Configuration.getMySQLip() + "/" + Configuration.getMySQLDatabase();
            String local_username = Configuration.getMySQLUsername();
            String local_password = Configuration.getMySQLPassword();

            Connection conn = DriverManager.getConnection(local_url,local_username,local_password);
            Statement statement = conn.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  null;
    }

    void classCall() {
        //Set the Driver class for JDBC MySQL
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
