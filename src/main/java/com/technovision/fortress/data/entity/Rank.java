package com.technovision.fortress.data.entity;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.Entity;

@Entity(name = "ranks")
public class Rank {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private String name;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Group group;

    @DatabaseField(defaultValue = "false")
    private boolean doorsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean chestsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean containersPermission;

    @DatabaseField(defaultValue = "false")
    private boolean bedsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean blocksPermission;

    @DatabaseField(defaultValue = "false")
    private boolean adminsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean modsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean membersPermission;

    @DatabaseField(defaultValue = "false")
    private boolean passwordPermission;

    @DatabaseField(defaultValue = "false")
    private boolean subgroupPermission;

    @DatabaseField(defaultValue = "false")
    private boolean permsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean deletePermission;

    @DatabaseField(defaultValue = "false")
    private boolean mergePermission;

    @DatabaseField(defaultValue = "false")
    private boolean cropsPermission;

    @DatabaseField(defaultValue = "false")
    private boolean snitchNamePermission;

    @DatabaseField(defaultValue = "false")
    private boolean snitchViewPermission;

    @DatabaseField(defaultValue = "false")
    private boolean linkingPermission;

    @DatabaseField(defaultValue = "false")
    private boolean snitchImmunePermission;

    public Rank() { }

    public Rank(String name, Group group) {
        this.name = name;
        this.group = group;
        if (name.equalsIgnoreCase("member")) {
            // Set perms for members
            doorsPermission = true;
            chestsPermission = true;
            containersPermission = true;
            bedsPermission = true;
            snitchImmunePermission = true;
            snitchViewPermission = true;
        }
        else if (name.equalsIgnoreCase("moderator")) {
            // Set perms for mods
            doorsPermission = true;
            chestsPermission = true;
            containersPermission = true;
            bedsPermission = true;
            blocksPermission = true;
            membersPermission = true;
            cropsPermission = true;
            snitchImmunePermission = true;
            snitchViewPermission = true;
        }
        else if (name.equalsIgnoreCase("admin")) {
            // Set perms for admins
            doorsPermission = true;
            chestsPermission = true;
            containersPermission = true;
            bedsPermission = true;
            blocksPermission = true;
            modsPermission = true;
            membersPermission = true;
            passwordPermission = true;
            cropsPermission = true;
            snitchNamePermission = true;
            snitchImmunePermission = true;
            snitchViewPermission = true;
        }
        else {
            // Set perms for owner
            doorsPermission = true;
            chestsPermission = true;
            containersPermission = true;
            bedsPermission = true;
            blocksPermission = true;
            adminsPermission = true;
            modsPermission = true;
            membersPermission = true;
            passwordPermission = true;
            subgroupPermission = true;
            permsPermission = true;
            deletePermission = true;
            mergePermission = true;
            linkingPermission = true;
            cropsPermission = true;
            snitchNamePermission = true;
            snitchImmunePermission = true;
            snitchViewPermission = true;
        }
    }

    public boolean hasPermission(String permission) {
        if (permission.equalsIgnoreCase("BLOCKS")) {
            return blocksPermission;
        }
        if (permission.equalsIgnoreCase("DOORS")) {
            return doorsPermission;
        }
        if (permission.equalsIgnoreCase("CHESTS")) {
            return chestsPermission;
        }
        if (permission.equalsIgnoreCase("CONTAINERS")) {
            return containersPermission;
        }
        if (permission.equalsIgnoreCase("BEDS")) {
            return bedsPermission;
        }
        if (permission.equalsIgnoreCase("ADMINS")) {
            return adminsPermission;
        }
        if (permission.equalsIgnoreCase("MODS")) {
            return modsPermission;
        }
        if (permission.equalsIgnoreCase("MEMBERS")) {
            return membersPermission;
        }
        if (permission.equalsIgnoreCase("PASSWORD")) {
            return passwordPermission;
        }
        if (permission.equalsIgnoreCase("SUBGROUP")) {
            return subgroupPermission;
        }
        if (permission.equalsIgnoreCase("PERMS")) {
            return permsPermission;
        }
        if (permission.equalsIgnoreCase("DELETE")) {
            return deletePermission;
        }
        if (permission.equalsIgnoreCase("MERGE")) {
            return mergePermission;
        }
        if (permission.equalsIgnoreCase("CROPS")) {
            return cropsPermission;
        }
        if (permission.equalsIgnoreCase("SNITCH_NAME")) {
            return snitchNamePermission;
        }
        if (permission.equalsIgnoreCase("SNITCH_IMMUNE")) {
            return snitchImmunePermission;
        }
        if (permission.equalsIgnoreCase("SNITCH_VIEW")) {
            return snitchViewPermission;
        }
        if (permission.equalsIgnoreCase("LINKING")) {
            return linkingPermission;
        }
        return false;
    }

    /** Getters */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    /** Setters */

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
