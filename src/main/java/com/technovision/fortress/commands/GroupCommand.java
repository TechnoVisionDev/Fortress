package com.technovision.fortress.commands;

import com.technovision.fortress.data.Database;
import com.technovision.fortress.data.entity.Group;
import com.technovision.fortress.Fortress;
import com.technovision.fortress.data.entity.Member;
import com.technovision.fortress.util.CKException;
import com.technovision.fortress.util.EffectUtils;
import com.technovision.fortress.util.MessageUtils;
import com.technovision.fortress.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles group commands.
 *
 * @author TechnoVision
 */
public class GroupCommand extends CommandBase {

    public GroupCommand(Fortress plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group";
        displayName = "Group";

        // Implemented
        commands.put("create", "<name> [type] [password] - Create a new group (defaults to private).");
        commands.put("invite", "<group> <player> - Invite a player to join a group.");
        commands.put("rescind", "<group> <player> - Cancel a player's invite to your group.");
        commands.put("join", "<group> [password] - Join a group that has invited you (or use a password).");
        commands.put("leave", "<group> - Leave a group that you are currently in.");
        commands.put("list", "[player] - List all groups that you or another player are in.");
        commands.put("invites", "List all groups that have invited you.");
        commands.put("remove", "<group> <player> - Remove a player from your group.");
        commands.put("perms", "Manage permissions for player ranks in a group.");
        commands.put("bio", "<group> <text> - Set a biography for a group (100 chars max).");
        commands.put("delete", "<group> - Delete a group you are currently in.");
        commands.put("info", "<group> - Display information about a group.");
        commands.put("promote", "<group> <player> <rank> - Promote or demote a player to a new rank.");
        commands.put("transfer", "<group> <player> - Transfer ownership of a group.");

        // Not Yet Implemented
        /**
         commands.put("link", "[super-group] [sub-group] - Link two groups together.");
         commands.put("unlink", "[super-group] [sub-group] - Unlink two groups from each other.");
         commands.put("merge", "[group] [merge-group] - Merges the first group into the second group.");
         */
    }

    public void create_cmd() throws CKException, SQLException {
        // Get group name from args
        if (args.length < 2) {
            throw new CKException("You must enter a name for your group.");
        }
        String name = StringUtils.toSafeString(args[1], 30, "Your group name");
        Player player = getPlayer();

        // Check if group name is taken
        List<Group> existingGroups = Database.groups.queryForEq("name", name);
        if (!existingGroups.isEmpty()) {
            throw new CKException("A group named "+ ChatColor.YELLOW+args[1]+ChatColor.RED+" already exists!");
        }

        // Get group type from args if specified
        boolean isPublic = false;
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("public")) {
                isPublic = true;
            } else if (!args[2].equalsIgnoreCase("private")) {
                throw new CKException("The group type specified is invalid. Use 'public' or 'private'.");
            }
        }

        // Get group password from args if specified
        String password = null;
        if (args.length >= 4) {
            if (isPublic) {
                throw new CKException("Only private groups can add a password!");
            }
            password = StringUtils.toSafeString(args[3], 30, "Your group password");
        }

        // Create group locally and in database
        Group group = new Group(name, player.getUniqueId(), isPublic, password);
        Database.groups.create(group);

        // Send success message & firework
        MessageUtils.send(player, " ");
        MessageUtils.sendHeading(player, "You Created a Group!");
        MessageUtils.send(player, createGroupMessage(group));
        EffectUtils.launchfirework(EffectUtils.greenFirework, player.getLocation());
    }

    public void invite_cmd() throws CKException, SQLException {
        // Get group from args
        Group group = getGroupFromArgs(1);
        if (group.isPublic()) {
            throw new CKException("You can only invite members to a private group!");
        }

        // Check if sender can invite
        Member member = getMember(group);
        if (member == null) {
            throw new CKException("You are not a member of that group!");
        }
        if (!member.hasPermission("MEMBERS")) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MEMBERS"+ChatColor.RED+" permission to invite players.");
        }

        // Get player to invite from args
        OfflinePlayer playerToInvite = getPlayerFromArgs(2);
        List<Member> memberToInvite = Database.members.queryForEq("playerId", playerToInvite.getUniqueId());
        for (Member m : memberToInvite) {
            if (m.getGroup().getName().equalsIgnoreCase(group.getName())) {
                throw new CKException("Your group has already invited that player to join!");
            }
        }

        // Invite player
        group.invitePlayer(playerToInvite.getUniqueId());
        Player invitedPlayer = Bukkit.getPlayer(playerToInvite.getUniqueId());
        if (invitedPlayer != null) {
            MessageUtils.send(invitedPlayer, ChatColor.GRAY + "You received an invite to join the group " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
            MessageUtils.send(invitedPlayer, ChatColor.GRAY + "Use the " + ChatColor.YELLOW + "/group join" + ChatColor.GRAY + " command to join!");
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You sent an invite to " + ChatColor.YELLOW + playerToInvite.getName() + ChatColor.GRAY + " to join " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
    }

    public void join_cmd() throws CKException, SQLException {
        Group group = getGroupFromArgs(1);
        Member member = getMember(group);
        if (member == null) {
            throw new CKException("You have not been invited by that group!");
        }
        if (!member.isInvited()) {
            throw new CKException("You are already a member of that group!");
        }

        // TODO: Check for things like public groups and passwords

        group.acceptPlayer(member);
        sendMessageToGroup(group, String.format("%s%s%s has joined the group %s%s",
                ChatColor.YELLOW,
                Bukkit.getPlayer(member.getPlayerId()).getName(),
                ChatColor.GRAY,
                ChatColor.YELLOW,
                group.getName()
        ));
    }

    private String[] createGroupMessage(Group group) {
        String[] msg = {
                ChatColor.GREEN + "You created a " + ChatColor.YELLOW + (group.isPublic() ? "public" : "private") + ChatColor.GREEN + " group named " + ChatColor.YELLOW + group.getName() + ChatColor.GREEN + ".",
                ChatColor.GREEN + "Players must enter a password to join this group.",
                " ",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group" + ChatColor.GRAY + " to manage your group.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group invite" + ChatColor.GRAY + " to invite other players.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group set" + ChatColor.GRAY + " to set a display name and bio.",
                " "
        };
        // If password is null, compact the array to exclude the empty password line
        if (group.getPassword() == null) {
            msg = new String[] {
                    msg[0], // Group creation message
                    msg[2], // Blank line
                    msg[3], // /group message
                    msg[4], // /group invite message
                    msg[5], // /group set message
                    msg[6]  // Blank line
            };
        }
        return msg;
    }

    @Override
    public void doDefaultAction() throws CKException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CKException {
    }
}
