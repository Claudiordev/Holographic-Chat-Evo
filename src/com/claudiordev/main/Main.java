package com.claudiordev.main;

import com.claudiordev.config.Configuration;
import com.claudiordev.data.MySQLData;
import com.claudiordev.data.SQLLiteData;
import com.claudiordev.files.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;
    private static PluginManager pm;
    //Bukkit Native, used to edit the File above (Java Native), set Data, retrieve Data etc..
    private static boolean toggleState = true;

    /** Main Method **/
    private static void main(String args[]) {

    }

    /** Method Launched on Load of Server **/
    public void onEnable() {
        plugin = this;

        loadConfig();

        loadMessages();

        //Get the Plugin Manager
        pm = getServer().getPluginManager();

        //Register a new event, of the Class Actions as a New Instance of it, on the plugin variable referent to Skills,
        pm.registerEvents(new Actions(), plugin);

        //Register the command "/hdchat", Set an instance of the Commands Class as executor
        this.getCommand("hdchat").setExecutor(new Commands());

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            getLogger().severe("* ProtocolLib is not installed or enabled. *");
            getLogger().severe("* The \"/hdchat toggle\" function will be disabled*");
            toggleState = false;
        }

        switch (Configuration.getDataType()) {
            case 1:
                SQLLiteData sqlLiteData = new SQLLiteData("jdbc:sqlite:plugins/HolographicChatEvo/db/Data.db");
                sqlLiteData.connect("org.sqlite.JDBC");
                sqlLiteData.createTable("data");
                Main.getPlugin().getLogger().info("SQLLite Connection detected");
                break;
            case 2:
                MySQLData mySQLData = new MySQLData(Configuration.getMySQLUsername(), Configuration.getMySQLPassword());
                mySQLData.connect("com.mysql.jdbc.Driver");
                mySQLData.createTable("data");
                Main.getPlugin().getLogger().info("MySQL Connection detected");
                break;
            default:
                Main.getPlugin().getLogger().info("No Data Type (1 or 2) chosen, disabling the plugin; \n Correct the config.yml file and restart the server!");
                //Disable the Server;
                this.setEnabled(false);
                break;
        }
    }

    public void onDisable() {
        switch (Configuration.getDataType()) {
            case 1:
                break;
            case 2:
                break;
        }
    }

    /**
     * Load the Configuration file in Default way (config.yml on the project structure)
     */
    private void loadConfig() {
        //Default way of loading Configuration File
        new Configuration();
        Configuration.load();
        saveDefaultConfig();
    }

    /**
     * Load a "messages.yml" file
     */
    private void loadMessages() {
        MessagesFile messagesFile = new MessagesFile(getDataFolder(),"messages.yml");
        messagesFile.loadMessages();
    }

    /**
     * @return  this class in everywhere on the code
     */
    public static Main getPlugin() {
        return plugin;
    }

    /**
     * @return The state of the Toggle Command, depending if the dependencies where found or not;
     */
    public static boolean isToggleState() {
        return toggleState;
    }
}
