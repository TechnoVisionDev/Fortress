package com.technovision.fortress.util;

import com.technovision.fortress.data.entity.Group;
import com.technovision.fortress.data.entity.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void send(Object sender, String line) {
        if ((sender instanceof Player)) {
            ((Player) sender).sendMessage(line);
        } else if (sender instanceof CommandSender) {
            ((CommandSender) sender).sendMessage(line);
        }
    }
    public static void send(Object sender, String[] lines) {
        boolean isPlayer = false;
        if (sender instanceof Player)
            isPlayer = true;

        for (String line : lines) {
            if (isPlayer) {
                ((Player) sender).sendMessage(line);
            } else {
                ((CommandSender) sender).sendMessage(line);
            }
        }
    }

    public static void sendGroup(Group group, String message) {
        message = String.format("%s[%s%s%s] %s", ChatColor.GRAY, ChatColor.GOLD, group.getName(), ChatColor.GRAY, message);
        for (Member member : group.getMembers()) {
            Player player = Bukkit.getPlayer(member.getPlayerId());
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public static void sendError(Object sender, String line) {
        send(sender, ChatColor.RED+line);
    }

    public static void sendSuccess(CommandSender sender, String message) {
        send(sender, ChatColor.GREEN+message);
    }

    public static void sendHeading(CommandSender sender, String title) {
        send(sender, buildTitle(title));
    }

    public static String buildTitle(String title) {
        String line =   "-------------------------------------------------";
        String titleBracket = "[ " + ChatColor.GOLD + title + ChatColor.AQUA + " ]";

        if (titleBracket.length() > line.length()) {
            return ChatColor.AQUA+"-"+titleBracket+"-";
        }

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = ChatColor.AQUA + line.substring(0, min);
        out += titleBracket + line.substring(max);

        return out;
    }

    public static String buildSmallTitle(String title) {
        String line = ChatColor.AQUA+"------------------------------";

        String titleBracket = "[ "+title+" ]";

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = ChatColor.AQUA + line.substring(0, Math.max(0, min));
        out += titleBracket + line.substring(max);

        return out;
    }
}
