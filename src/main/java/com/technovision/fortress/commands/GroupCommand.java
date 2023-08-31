package com.technovision.fortress.commands;

import com.technovision.fortress.data.Database;
import com.technovision.fortress.data.entity.Group;
import com.technovision.fortress.Fortress;
import com.technovision.fortress.data.entity.Member;
import com.technovision.fortress.handlers.InventoryHandler;
import com.technovision.fortress.util.CKException;
import com.technovision.fortress.util.EffectUtils;
import com.technovision.fortress.util.MessageUtils;
import com.technovision.fortress.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
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
        commands.put("invites", "List all groups that have invited you.");
        commands.put("list", "[player] - List all groups that you or another player are in.");

        // Not Yet Implemented
        /**
         commands.put("leave", "<group> - Leave a group that you are currently in.");
         commands.put("remove", "<group> <player> - Remove a player from your group.");
         commands.put("perms", "Manage permissions for player ranks in a group.");
         commands.put("bio", "<group> <text> - Set a biography for a group (100 chars max).");
         commands.put("delete", "<group> - Delete a group you are currently in.");
         commands.put("info", "<group> - Display information about a group.");
         commands.put("promote", "<group> <player> <rank> - Promote or demote a player to a new rank.");
         commands.put("transfer", "<group> <player> - Transfer ownership of a group.");
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
        List<Member> memberToInvite = getMemberFromPlayer(playerToInvite.getUniqueId(), group);
        if (!memberToInvite.isEmpty()) {
            throw new CKException("Your group has already invited that player to join!");
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
        Player player = getPlayer();
        Member member = getMember(group);
        String msg = String.format("%s%s%s has joined the group %s%s", ChatColor.YELLOW, player.getName(), ChatColor.GRAY, ChatColor.YELLOW, group.getName());

        if (member == null) {
            member = new Member(player.getUniqueId(), group, group.getMemberRank());
            if (group.isPublic()) {
                group.acceptPlayer(member);
                sendMessageToGroup(group, msg);
                MessageUtils.send(player, String.format("%s[%s%s%s] %s", ChatColor.GRAY, ChatColor.GOLD, group.getName(), ChatColor.GRAY, msg));
                return;
            }
            if (group.getPassword() != null) {
                if (args.length >= 3 && group.getPassword().equals(args[2])) {
                    group.acceptPlayer(member);
                    sendMessageToGroup(group, msg);
                    MessageUtils.send(player, String.format("%s[%s%s%s] %s", ChatColor.GRAY, ChatColor.GOLD, group.getName(), ChatColor.GRAY, msg));
                    return;
                } else {
                    throw new CKException("The password you entered is not correct or you haven't been invited!");
                }
            }
            throw new CKException("You have not been invited by that group!");
        }
        if (!member.isInvited()) {
            throw new CKException("You are already a member of that group!");
        }

        group.acceptPlayer(member);
        sendMessageToGroup(group, msg);
    }

    public void rescind_cmd() throws CKException, SQLException {
        // Get group from args
        Group group = getGroupFromArgs(1);
        if (group.isPublic()) {
            throw new CKException("That group is public and thus does not use invites!");
        }

        // Check if sender can rescind invites
        Member member = getMember(group);
        if (member == null || !member.hasRank()) {
            throw new CKException("You are not a member of that group!");
        }
        if (!member.hasPermission("MEMBERS")) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MEMBERS"+ChatColor.RED+" permission to rescind invites.");
        }

        // Get member to rescind invite from args
        if (args.length < 3) {
            throw new CKException("You must specify a player!");
        }
        OfflinePlayer invitedPlayer = getPlayerFromArgs(2);
        List<Member> invitedMembers = getMemberFromPlayer(invitedPlayer.getUniqueId(), group);
        if (invitedMembers.isEmpty()) {
            throw new CKException("That player doesn't have an invite!");
        }
        Member invitedMember = invitedMembers.get(0);
        if (invitedMember.hasRank()) {
            throw new CKException("That player is already in your group!");
        }

        // Rescind invite
        group.uninvitePlayer(invitedMember);

        // Send messages
        if (invitedPlayer.isOnline()) {
            MessageUtils.send(invitedPlayer, ChatColor.YELLOW + group.getName() + ChatColor.GRAY + " has rescinded their invite to you.");
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You rescinded an invite to " + ChatColor.YELLOW + invitedPlayer.getName() + ChatColor.GRAY + " from " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
    }

    public void invites_cmd() throws CKException {
        Player player = getPlayer();
        List<Member> members = getMemberFromPlayer(player.getUniqueId());

        // Get list of valid invites
        List<String> groupList = new ArrayList<>();
        for (Member member : members) {
            if (member.isInvited()) {
                String groupString = String.format("%s%s",
                        ChatColor.YELLOW,
                        member.getGroup().getName()
                );
                groupList.add(groupString);
            }
        }
        if (groupList.isEmpty()) {
            MessageUtils.send(sender, ChatColor.GRAY+"You do not have any group invites yet!");
            return;
        }

        // Send invites to player
        MessageUtils.sendHeading(player, "Your Invites");
        MessageUtils.send(player, groupList.toArray(new String[0]));
    }

    public void list_cmd() throws CKException {
        OfflinePlayer player;
        List<Member> members;
        if (args.length >= 2) {
            player = getPlayerFromArgs(1);
            if (player == null || !player.hasPlayedBefore()) {
                throw new CKException("The player you specified doesn't exist.");
            }
            members = getMemberFromPlayer(player.getUniqueId());
        } else {
            player = getPlayer();
            members = getMemberFromPlayer(player.getUniqueId());
        }

        int pageSize = 45; // Number of items per page
        int totalPages = (int) Math.ceil((double) members.size() / pageSize);

        List<Inventory> pages = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            Inventory page = Bukkit.createInventory(null, 54, "Groups - Page " + (i + 1));
            pages.add(page);
        }

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            Group group = member.getGroup();
            if (group != null) {

                Material mat;
                if (member.getRank().getName().equalsIgnoreCase("member")) {
                    mat = Material.LEATHER_CHESTPLATE;
                } else if (member.getRank().getName().equalsIgnoreCase("moderator")) {
                    mat = Material.IRON_CHESTPLATE;
                } else if (member.getRank().getName().equalsIgnoreCase("admin")) {
                    mat = Material.GOLDEN_CHESTPLATE;
                } else {
                    mat = Material.DIAMOND_CHESTPLATE;
                }

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + group.getName());
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                String rankName = member.getRank().getName().substring(0, 1).toUpperCase() + member.getRank().getName().substring(1);
                meta.setLore(List.of(ChatColor.GRAY + rankName));
                item.setItemMeta(meta);

                int pageIndex = i / pageSize;
                int slot = i % pageSize;
                pages.get(pageIndex).setItem(slot, item);
            }
        }

        for (int i = 0; i < totalPages; i++) {
            Inventory page = pages.get(i);

            if (i > 0) {
                ItemStack previousPageItem = new ItemStack(Material.ARROW);
                ItemMeta previousPageMeta = previousPageItem.getItemMeta();
                previousPageMeta.setDisplayName(ChatColor.WHITE + "Previous Page");
                previousPageItem.setItemMeta(previousPageMeta);
                page.setItem(45, previousPageItem);
            }

            if (i < totalPages - 1) {
                ItemStack nextPageItem = new ItemStack(Material.ARROW);
                ItemMeta nextPageMeta = nextPageItem.getItemMeta();
                nextPageMeta.setDisplayName(ChatColor.WHITE + "Next Page");
                nextPageItem.setItemMeta(nextPageMeta);
                page.setItem(53, nextPageItem);
            }
        }
        InventoryHandler.inventoryPages.put(player.getUniqueId(), pages);
        getPlayer().openInventory(pages.get(0));
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
