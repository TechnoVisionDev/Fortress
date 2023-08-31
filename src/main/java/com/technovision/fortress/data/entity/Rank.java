package com.technovision.fortress.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.technovision.fortress.data.enums.Permissions;

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

    public boolean hasPermission(Permissions permission) {
        switch (permission) {
            case BLOCKS -> {
                return blocksPermission;
            }
            case DOORS -> {
                return doorsPermission;
            }
            case CHESTS -> {
                return chestsPermission;
            }
            case CONTAINERS -> {
                return containersPermission;
            }
            case BEDS -> {
                return bedsPermission;
            }
            case ADMINS -> {
                return adminsPermission;
            }
            case MODS -> {
                return modsPermission;
            }
            case MEMBERS -> {
                return membersPermission;
            }
            case PASSWORD -> {
                return passwordPermission;
            }
            case SUBGROUP -> {
                return subgroupPermission;
            }
            case PERMS -> {
                return permsPermission;
            }
            case DELETE -> {
                return deletePermission;
            }
            case MERGE -> {
                return mergePermission;
            }
            case CROPS -> {
                return cropsPermission;
            }
            case SNITCH_NAME -> {
                return snitchNamePermission;
            }
            case SNITCH_IMMUNE -> {
                return snitchImmunePermission;
            }
            case SNITCH_VIEW -> {
                return snitchViewPermission;
            }
            case LINKING -> {
                return linkingPermission;
            }
        }
        return false;
    }

    public boolean isMember() {
        return name.equalsIgnoreCase("member");
    }

    public boolean isModerator() {
        return name.equalsIgnoreCase("moderator");
    }

    public boolean isAdmin() {
        return name.equalsIgnoreCase("admin");
    }

    public boolean isOwner() {
        return name.equalsIgnoreCase("owner");
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
