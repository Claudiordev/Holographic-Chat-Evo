package com.claudiordev.main;

import com.claudiordev.config.Configuration;
import com.claudiordev.data.DataHandler;
import com.claudiordev.data.SQLLiteData;
import com.claudiordev.utils.HologramHandler;
import com.claudiordev.utils.RadiusHandler;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.sql.SQLException;
import java.util.HashMap;

public class Actions implements Listener {
    //Player Name (Key), Hologram; Hologram List
    static HashMap<String, Hologram> hologramas = new HashMap<>();
    //Player Name (Key), Visibilit(Boolean)
    static HashMap<Player, Boolean> hologramas_visibility = new HashMap<>();
    //Player Name (Key), HologramHandler
    static HashMap<String, HologramHandler> hologramThreads = new HashMap<>();
    //RadiusHandler (Key), Player Name
    static HashMap<RadiusHandler, String> radiusHandlers = new HashMap<>();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Hologram hologram = HologramsAPI.createHologram(Main.getPlugin(), player.getLocation().add(0, Configuration.getHologram_height(), 0));
        hologramas.put(player.getName(), hologram);

        //On Join Data Handle
        try {
            String query = "SELECT uniqueid FROM data WHERE uniqueid = '"+ player.getUniqueId().toString() +"'";
            String playerid = player.getUniqueId().toString();

            if (((String) DataHandler.retrieveData(query,"uniqueid")) == null) {
                hologramas_visibility.put(player, true);
                DataHandler.handleInsert(playerid,1);

            } else if (((String) DataHandler.retrieveData(query,"uniqueid")).equals(playerid)) {
                //Retrieve later
                String retrieve_query = "SELECT state FROM data WHERE uniqueid = '" + playerid +"'";

                if ((DataHandler.retrieveData(retrieve_query, "state")).equals("1")) {
                    hologramas_visibility.put(player, true);
                } else if ((DataHandler.retrieveData(retrieve_query, "state")).equals("0")) {
                    hologramas_visibility.put(player, false);
                }
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        //Log
        //plugin.getLogger().info("New Chat Hologram added to the list, Player: " + player.getName());

        //Radius Handler checker;
        if (Configuration.isRadius()) {
            RadiusHandler radiusHandler = new RadiusHandler(player,hologram);
            radiusHandlers.put(radiusHandler,player.getName());
            radiusHandler.start();
        }
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        //plugin.getLogger().info("Movement detected from player: " + player.getLocation());
        for (String name : hologramas.keySet()) {

            //If name on Holograms Hash Map equals to Name of Moving Player, then get respective Hologram of it
            if (name.equals(player.getName())) {
                Hologram holograma = hologramas.get(name);
                holograma.teleport(player.getLocation().add(0,Configuration.getHologram_height(),0));
                //plugin.getLogger().info("Hologram teleported to: " + player.getLocation().add(0, 4, 0));
            }
        }
    }

    /**
     * Set visibility Action on all the existent Holograms
     * @param player Player invocating the action;
     * @param state true or false to show or hide;
     */
    public static void setVisibility(Player player, boolean state) {
        for (Hologram hologram : hologramas.values()) {
            if (state) {
                hologram.getVisibilityManager().showTo(player.getPlayer());
                hologramas_visibility.put(player, true);
            } else {
                hologram.getVisibilityManager().hideTo(player.getPlayer());
                hologramas_visibility.put(player, false);
            }
        }
    }

    /**
     * Player Quit Event, Write on SQLLite Data;
     * @param e
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        //Clear Hologram On Leave
        for (String name : hologramas.keySet()) {
            if (name.equals(player.getName())) {
                hologramas.get(name).clearLines();
            }
        }

        //Clear HologramHandler Thread from the list
        for (String name: hologramThreads.keySet()) {
            if (name.equals(player.getName())) {
                HologramHandler hologramHandler = hologramThreads.get(name);
                hologramHandler = null;
                hologramThreads.remove(name);
            }
        }

        //Data Update to Database
        for (Player playerCheck: hologramas_visibility.keySet()) {
            if (playerCheck.getName().equals(player.getName())) {
                if (hologramas_visibility.get(playerCheck)) {
                    try {
                        DataHandler.manageData("UPDATE data SET state = 1 WHERE uniqueid = '" + player.getUniqueId().toString() + "';");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        DataHandler.manageData("UPDATE data SET state = 0 WHERE uniqueid = '" + player.getUniqueId().toString() + "';");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Chat Event
     * @param e Player Event
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        int size = message.length();

        if (!message.startsWith("/")) {

            if (Configuration.getSpecial_chat()) {
                if (message.startsWith("!")) {
                    message = message.replaceFirst("!", "");
                    produceHologram(message, player,size);
                    e.setMessage(message);
                }
            } else {
                produceHologram(message, player,size);
            }
        }

        //Cancel the Text Chat on Type;
        if (Configuration.getChat_type() == 1) {
            e.setCancelled(true);
        }
    }

    /**
     * Produce the Hologram text lines in order
     * @param message New line message
     * @param player Player activating the event
     * @param size Size of the message
     */
    void produceHologram(String message, Player player, int size) {
        HologramHandler hologramHandler = null;
        for (String name : hologramas.keySet()) {

            //If name on Holograms Hash Map equals to Name of Moving Player, then get respective Hologram of it
            if (name.equals(player.getName())) {
                Hologram holograma = hologramas.get(name);

                for (String player_threadName : hologramThreads.keySet()) {
                    if (player_threadName.equals(name)) {
                        hologramHandler = hologramThreads.get(player_threadName);
                    }
                }

                if (hologramHandler == null) {
                    hologramHandler = new HologramHandler(holograma, size);
                    hologramThreads.put(player.getName(), hologramHandler);
                    hologramHandler.start();
                } else {
                    hologramHandler.resume();
                }

                if (holograma.size() < 1) {
                    TextLine textLine = holograma.appendTextLine(net.md_5.bungee.api.ChatColor.AQUA + player.getName() + ChatColor.GOLD + " " + Configuration.getHologram_prefix());
                    holograma.appendTextLine(ChatColor.YELLOW + message);

                } else if (holograma.size() > 1) {
                    if (holograma.size() < Configuration.getHologram_text_lines() + 1) {
                        holograma.appendTextLine(ChatColor.YELLOW + message);
                    } else {
                        holograma.removeLine(1);
                        holograma.appendTextLine(ChatColor.YELLOW + message);
                    }
                }

            }
        }
    }

    /**
     * Bug Fix, used to clear the Hologram on changing the world;
     * It will be with the lines on memory and it will not start with the prefix;
     * @param e
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();

        for (String name : hologramas.keySet()) {
            if (name.equals(player.getName())) {
                hologramas.get(name).clearLines();
            }
        }
    }

    public static HashMap<String, HologramHandler> getHologramThreads() {
        return hologramThreads;
    }

    public static HashMap<String, Hologram> getHologramas() {
        return hologramas;
    }

    public static HashMap<Player, Boolean> getHologramas_visibility() {
        return hologramas_visibility;
    }
}