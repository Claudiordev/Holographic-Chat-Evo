package com.claudiordev.files;

import java.io.File;

public class MessagesFile extends AbstractFile {

    public MessagesFile(File dataFolder, String fileName) {
        //Calls the AbstractFile construct and use uppon this class;
        super(dataFolder, fileName);
    }

    /**
     * Initiate the load of the Default values and save them into the respective file;
     */
    public void loadMessages() {
        this.add("Hologram-Activated", "&7Hologram Activated!");
        this.add("Hologram-Deactivated", "&7Hologram Deactivated!");
        this.add("Cmd-Only-Player", "This command can only be executed by a Player!");
        this.add("Cmd-Error", "&cError, this command it is not working!");
        this.add("Data-Argument-Missing", "&cError, argument missing, please type the destiny (MySQL or SQLLite)");
        this.add("Data-Wrong-Argument", "&cError, wrong argument, please type the destiny (MySQL or SQLLite)");
        this.add("Data-SQLLite-Import-Success", "&aImported data to SQLLite with success!");
        this.add("Data-SQLLite-Import-Start", "&7Starting to import MySQL data into SQLLite Database...");
        this.add("Data-MySQL-Import-Success", "&aImported data to MySQL with success!");
        this.add("Data-MySQL-Import-Start", "&7Starting to import SQLLite data into MySQL Database...");
        this.add("Data-MySQL-Connect-Error", "&cError connection to the Database, please check the config.yml ands logs, and try again!");
        this.save();
    }
}
