package com.claudiordev.data;

import com.claudiordev.files.MessagesFile;
import com.claudiordev.utils.ColorCodes;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImportHandler extends Thread {

    ResultSet rs;
    MySQLData mySQLData = new MySQLData();
    SQLLiteData sqlLiteData = new SQLLiteData();
    Player player;
    ColorCodes colorCodes = new ColorCodes();
    String type;

    public ImportHandler(ResultSet rs, Player player, String type) {
        this.rs = rs;
        this.player = player;
        this.type = type;
    }


    @Override
    public void run() {
            try {
                switch (type) {
                    case "MySQL":
                        while (rs.next()) {
                            mySQLData.insertImport(rs.getString("uniqueid"),rs.getInt("state"));
                        }
                        player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-MySQL-Import-Success")));
                        break;
                    case "SQLLite":
                        while (rs.next()) {
                            sqlLiteData.insertImport(rs.getString("uniqueid"),rs.getInt("state"));
                        }
                        player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-SQLLite-Import-Success")));
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
}
