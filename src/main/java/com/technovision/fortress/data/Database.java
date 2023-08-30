package com.technovision.fortress.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.technovision.fortress.data.entity.Group;
import com.technovision.fortress.data.entity.Member;
import com.technovision.fortress.data.entity.Rank;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

public class Database {

    public ConnectionSource connectionSource;
    public static Dao<Group, Integer> groups;
    public static Dao<Member, Integer> members;
    public static Dao<Rank, Integer> ranks;

    public Database(FileConfiguration config) throws SQLException {
        // Get connection string from config
        String hostname = config.getString("mysql.hostname");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String userid = config.getString("mysql.userid");
        String password = config.getString("mysql.password");

        // Connect to database
        String databaseUrl = "jdbc:mysql://" + userid + ":" + password + "@" + hostname + ":" + port + "/" + database;
        connectionSource = new JdbcConnectionSource(databaseUrl);

        // Create tables
        groups = DaoManager.createDao(connectionSource, Group.class);
        members = DaoManager.createDao(connectionSource, Member.class);
        ranks = DaoManager.createDao(connectionSource, Rank.class);

        TableUtils.createTableIfNotExists(connectionSource, Group.class);
        TableUtils.createTableIfNotExists(connectionSource, Member.class);
        TableUtils.createTableIfNotExists(connectionSource, Rank.class);
    }
}
