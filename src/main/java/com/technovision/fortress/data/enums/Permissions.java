package com.technovision.fortress.data.enums;

/**
 * Enums containing data for permissions assigned to group ranks.
 *
 * @author TechnoVision
 */
public enum Permissions {
    DOORS("Can open and close reinforced doors"),
    CHESTS("Can open and close reinforced chests"),
    CONTAINERS("Can open and close reinforced containers"),
    BEDS("Can sleep in reinforced beds"),
    BLOCKS("Can reinforce blocks to the group or bypass existing reinforced blocks"),
    ADMINS("Can add or remove admins"),
    MODS("Can add or remove mods"),
    MEMBERS("Can add or remove members"),
    PASSWORD("Can add or remove password to the group"),
    SUBGROUP("Can add subgroup"),
    PERMS("Can modify the permissions a PlayerType has"),
    DELETE("Can delete the group"),
    JOIN_PASSWORD("Can specify which PlayerType a player will be, when they join with a password"),
    MERGE("Can merge groups with another group that has the MERGE permissions on"),
    CROPS("Allows access to reinforced crops"),
    SNITCH_NAME("Allows player to name group snitches"),
    SNITCH_VIEW("Allows player to view snitch logs"),
    LINKING("Can nest and un-nest the group"),
    SNITCH_IMMUNE("Player will not trigger snitch for this group");

    private final String description;

    Permissions(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
