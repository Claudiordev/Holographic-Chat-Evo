package com.claudiordev.main;

import com.claudiordev.config.Configuration;
import com.claudiordev.data.ImportHandler;
import com.claudiordev.data.MySQLData;
import com.claudiordev.data.SQLLiteData;
import com.claudiordev.files.MessagesFile;
import com.claudiordev.utils.ColorCodes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    ColorCodes colorCodes = new ColorCodes();
    MySQLData mySQLData = new MySQLData();
    SQLLiteData sqlLiteData = new SQLLiteData();

    /**
     *
     * @param commandSender represents whatever is sending the command.
     *                      This could be a Player, ConsoleCommandSender, or BlockCommandSender (a command block)
     * @param command represents which is the command being called. This will almost always be known ahead of time.
     * @param label represents the exact first word of the command (excluding arguments) that was entered by the sender
     * @param strings is the remainder of the command statement (the arguments after the label) split up by spaces and put into an array.
     * @return true if the command is executed
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (label.equals("hdchat")) {
            if (strings.length > 0) {
                switch (strings[0]) {
                    case "toggle":
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            if (Main.isToggleState()) {
                                if (Actions.getHologramas_visibility().get(player)) {
                                    Actions.setVisibility(player, false);
                                    player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Hologram-Deactivated")));
                                } else {
                                    Actions.setVisibility(player, true);
                                    player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Hologram-Activated")));
                                }
                            } else {
                                player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Cmd-Error")));
                            }
                        } else {
                            Main.getPlugin().getLogger().info(MessagesFile.getFileConfiguration().getString("Cmd-Only-Player"));
                        }
                    break;

                    case "import":
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;

                                if (strings.length > 1 && strings[1] != null) {
                                    switch(strings[1]) {
                                     case "SQLLite":
                                         //MySQL to SQLLite success message;
                                             if (mySQLData.tryConnection()) {
                                                 player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-SQLLite-Import-Start")));
                                                 sqlLiteData.deletedir();
                                                 sqlLiteData.createdir();
                                                 sqlLiteData.createTable("data");
                                                 ImportHandler importHandler = new ImportHandler(mySQLData.retrieveAllData("SELECT * FROM data", "org.sqlite.JDBC"), player, "SQLLite");
                                                 importHandler.start();
                                             } else {
                                                 player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-MySQL-Connect-Error")));
                                             }
                                        break;
                                     case "MySQL":
                                         //SQLLite to MySQL
                                         if (mySQLData.tryConnection()) {
                                             //Check if there is any SQLLite file()
                                             player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-MySQL-Import-Start")));
                                             mySQLData.dropTable("data");
                                             mySQLData.createTable("data");
                                             ImportHandler importHandler = new ImportHandler(sqlLiteData.retrieveAllData("SELECT * FROM data"), player, "MySQL");
                                             importHandler.start();
                                         } else {
                                             player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-MySQL-Connect-Error")));
                                         }
                                         break;
                                     default:
                                        player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-Wrong-Argument")));
                                        break;
                                     }
                                } else {
                                    player.sendMessage(colorCodes.executeReplace(MessagesFile.getFileConfiguration().getString("Data-Argument-Missing")));
                                }
                        } else {
                            Main.getPlugin().getLogger().info(MessagesFile.getFileConfiguration().getString("Cmd-Only-Player"));
                        }
                    break;
                }
            } else {
                //If non argument stated, show the help message defined on the config.yml
                for (String e: Configuration.getHelp_message()) {
                    //player.sendMessage(new ColorCodes().executeReplace(e));
                    commandSender.sendMessage(colorCodes.executeReplace(e));
                }
            }
        }

        //If true it will run the command silently, without the notification of the tip of Bukkit
        return true;
    }
}
