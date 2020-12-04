package com.claudiordev.data;

import com.claudiordev.main.Main;

import java.sql.*;

public abstract class AbstractData {

    static String url, username, password;
    static String tablename;

    /**
     * First Connection;
     * Establishes Connection to the SQLLite Database and closes it;
     */
    public void connect(String class_name) {
        Connection conn = null;
        try {
            //Set the Driver class for JDBC SQlLite
            Class.forName(class_name);


            // create a connection to the database
            conn = DriverManager.getConnection(url, username, password);

            Main.getPlugin().getLogger().info("Connection to database has been established;");

        } catch (SQLException | ClassNotFoundException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.getPlugin().getLogger().info(ex.getMessage());
            }
        }
    }

    /**
     * Used to Do Updates or Inserts on the SQLLite database;
     * @param query
     */
    public void manageData(String query) throws SQLException{
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement stmt = conn.createStatement();
        stmt.execute(query);
        conn.close();
    }

    public void insertData(String uniqueid, Integer state) {
        String query = "INSERT INTO data (uniqueid,state) \n"
                + "VALUES('" +uniqueid + "', "+state+");";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);

            Main.getPlugin().getLogger().info("Data inserted with success;");

            //Closes conn automatically after Try/Catch;
        } catch (SQLException e) {
            Main.getPlugin().getLogger().info(e.getMessage());
        }
    }


    public Object retrieveData(String query, String type) throws Exception{
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement stmt = conn.createStatement();
        String value;

        try {

            //Error check;
            if (!conn.isClosed()) {
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    value = rs.getString(type);
                    return value;
                }

            } else {
                throw new Exception("Error on Data Writting, please contact the creator of the Plugin!");
            }


        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Main.getPlugin().getLogger().info(ex.getMessage());
            }
        }

        //Return Null if no value is taken on the while loop
        return null;
    }
}
