package com.technovision.fortress;

import com.technovision.fortress.commands.GroupCommand;
import com.technovision.fortress.data.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Fortress plugin for Minecraft Bukkit/Spigot.
 *
 * @author TechnoVision
 */
public class Fortress extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");
    public Database database;

    @Override
    public void onEnable() {
        // Load config.yml file
        saveDefaultConfig();
        try {
            database = new Database(getConfig());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.severe(String.format("[%s] - You must specify MySQL database in config.yml!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        GroupCommand groupCommand = new GroupCommand(this);
        this.getCommand("group").setExecutor(groupCommand);

        // Register event handlers
        //getServer().getPluginManager().registerEvents(new MemberHandler(), this);

        logger.info(String.format("[%s] - Successfully loaded!", getDescription().getName()));
    }

    @Override
    public void onDisable() {
        // Close database connection
        try {
            database.connectionSource.close();
            logger.info(String.format("[%s] - Successfully disabled!", getDescription().getName()));
        } catch (Exception e) {
            logger.severe(String.format("[%s] - The database did not close properly!", getDescription().getName()));
        }
    }
}
