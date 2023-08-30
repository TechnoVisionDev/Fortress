package com.technovision.fortress.commands;

import com.technovision.fortress.Fortress;
import com.technovision.fortress.data.Database;
import com.technovision.fortress.data.entity.Group;
import com.technovision.fortress.data.entity.Member;
import com.technovision.fortress.util.CKException;
import com.technovision.fortress.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class CommandBase implements CommandExecutor {

    protected HashMap<String, String> commands = new HashMap<>();

    protected String[] args;
    protected CommandSender sender;

    protected String command;
    protected String displayName;
    protected boolean sendUnknownToDefault = false;
    protected Fortress plugin;

    public CommandBase(Fortress plugin) {
        this.plugin = plugin;
        init();
    }

    public Player getPlayer() throws CKException {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new CKException("Only players can run that command!");
    }

    public abstract void init();

    /* Called when no arguments are passed. */
    public abstract void doDefaultAction() throws CKException;

    /* Called on syntax error. */
    public abstract void showHelp();

    /* Called before command is executed to check permissions. */
    public abstract void permissionCheck() throws CKException;

    public Group getGroupFromArgs(int index) throws CKException {
        try {
            if (args.length < index + 1) {
                throw new CKException("You must specify a group name!");
            }
            List<Group> groups = Database.groups.queryForEq("name", args[index]);
            if (groups.isEmpty()) {
                throw new CKException("The group " + ChatColor.YELLOW + args[index] + ChatColor.RED + " doesn't exist!");
            }
            return groups.get(0);
        } catch (SQLException e) {
            throw new CKException("Unable to connect to database! Contact an admin!");
        }
    }

    public Member getMember(Group group) throws CKException {
        try {
            if (!(sender instanceof Player player)) {
                return null;
            }
            List<Member> groupMembers = Database.members.queryBuilder()
                    .where()
                    .eq("group_id", group)
                    .and()
                    .eq("playerId", player.getUniqueId())
                    .query();
            if (!groupMembers.isEmpty()) {
                return groupMembers.get(0);
            }
            return null;
        } catch (SQLException e) {
            throw new CKException("Unable to connect to database! Contact an admin!");
        }
    }

    protected OfflinePlayer getPlayerFromArgs(int index) throws CKException {
        if (args.length < (index+1)) {
            throw new CKException("You must enter a player's name!");
        }
        String name = args[index].toLowerCase();
        name = name.replace("%", "(\\w*)");

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.hasPlayedBefore()) {
            throw new CKException("The player you specified doesn't exist!");
        }
        return offlinePlayer;
    }

    protected List<Member> getMemberFromPlayer(UUID playerId) throws CKException {
        try {
            return Database.members.queryForEq("playerId", playerId);
        } catch (SQLException e) {
            throw new CKException("Unable to connect to database! Contact an admin!");
        }
    }

    protected List<Member> getMemberFromPlayer(UUID playerId, Group group) throws CKException {
        try {
            return Database.members.queryBuilder()
                    .where()
                    .eq("group_id", group)
                    .and()
                    .eq("playerId", playerId)
                    .query();
        } catch (SQLException e) {
            throw new CKException("Unable to connect to database! Contact an admin!");
        }
    }

    protected void sendMessageToGroup(Group group, String message) {
        message = String.format("%s[%s%s%s] %s", ChatColor.GRAY, ChatColor.GOLD, group.getName(), ChatColor.GRAY, message);
        for (Member member : group.getMembers()) {
            Player player = Bukkit.getPlayer(member.getPlayerId());
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        this.args = args;
        this.sender = sender;

        try {
            permissionCheck();
        } catch (Exception e) {
            MessageUtils.sendError(sender, e.getMessage());
            return false;
        }

        if (args.length == 0) {
            try {
                doDefaultAction();
                return true;
            } catch (CKException e) {
                MessageUtils.sendError(sender, e.getMessage());
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("help")) {
            showHelp();
            return true;
        }

        for (String c : commands.keySet()) {
            if (c.equalsIgnoreCase(args[0])) {
                try {
                    Method method = this.getClass().getMethod(args[0].toLowerCase() + "_cmd");
                    try {
                        method.invoke(this);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                        MessageUtils.sendError(sender, "Internal Command Error.");
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof CKException) {
                            MessageUtils.sendError(sender, e.getCause().getMessage());
                        } else {
                            MessageUtils.sendError(sender, "Internal Command Error.");
                            e.getCause().printStackTrace();
                        }
                    }
                } catch (NoSuchMethodException e) {
                    if (sendUnknownToDefault) {
                        try {
                            doDefaultAction();
                        } catch (CKException e1) {
                            MessageUtils.sendError(sender, e.getMessage());
                        }
                        return false;
                    }
                    MessageUtils.sendError(sender, "Unknown method " + args[0]);
                }
                return true;
            }
        }

        if (sendUnknownToDefault) {
            try {
                doDefaultAction();
                return true;
            } catch (CKException e) {
                MessageUtils.sendError(sender, e.getMessage());
            }
            return false;
        }

        MessageUtils.sendError(sender, "Unknown command " + args[0]);
        return true;
    }

    public void showBasicHelp() {
        MessageUtils.sendHeading(sender, displayName+" Command Help");
        for (String c : commands.keySet()) {
            String info = commands.get(c);

            info = info.replace("[", ChatColor.YELLOW+"[");
            info = info.replace("]", "]"+ChatColor.GRAY);
            info = info.replace("<", ChatColor.YELLOW+"<");
            info = info.replace(">", ">"+ChatColor.GRAY);

            MessageUtils.send(sender, ChatColor.GOLD+command+" "+c+ChatColor.GRAY+" "+info);
        }
    }
}
