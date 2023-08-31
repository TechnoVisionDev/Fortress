package com.technovision.fortress.commands;

import com.technovision.fortress.Fortress;

/**
 * Handles group permission commands.
 *
 * @author TechnoVision
 */
public class GroupPermsCommand extends CommandBase {

    public GroupPermsCommand(Fortress plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group perms";
        displayName = "Group Permissions";

        // Not implemented
        /**
         commands.put("add", "<group> <rank> <permission> - Add a permission to a group's player rank.");
         commands.put("remove", "<group> <rank> <permission> - Remove a permission from a group's player rank.");
         commands.put("list", "List all permissions.");
         commands.put("inspect", "[group] [rank] - View the permissions a group's rank has.");
         commands.put("reset", "[group] - Resets a group's permissions to default.");
         */
    }

    @Override
    public void doDefaultAction() {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() {
    }
}
