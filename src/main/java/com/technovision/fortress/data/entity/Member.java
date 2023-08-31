package com.technovision.fortress.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.technovision.fortress.data.Database;
import com.technovision.fortress.data.enums.Permissions;

import javax.persistence.Entity;
import java.sql.SQLException;
import java.util.UUID;

@Entity(name = "members")
public class Member {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private UUID playerId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Group group;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rank rank;

    public Member() { }

    public Member(UUID playerId, Group group, boolean isInvite) {
        this.playerId = playerId;
        this.group = group;
        if (isInvite) {
            this.rank = null;
        } else {
            this.rank = group.getMemberRank();
        }
    }

    public Member(UUID playerId, Group group, Rank rank) {
        this.playerId = playerId;
        this.group = group;
        this.rank = rank;
    }

    public boolean isInvited() {
        return rank == null;
    }

    public boolean hasRank() {
        return rank != null;
    }

    public boolean hasPermission(Permissions permission) {
        if (rank.getName().equalsIgnoreCase("owner")) return true;
        return rank.hasPermission(permission);
    }

    public boolean isOwner() {
        return rank.getName().equalsIgnoreCase("owner");
    }

    public boolean isAdmin() {
        return rank.getName().equalsIgnoreCase("admin");
    }

    public boolean isModerator() {
        return rank.getName().equalsIgnoreCase("moderator");
    }

    public boolean isMember() {
        return rank.getName().equalsIgnoreCase("member");
    }

    /** Getters */

    public int getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Group getGroup() {
        return group;
    }

    public Rank getRank() {
        return rank;
    }

    /** Setters */

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerId(UUID memberId) {
        this.playerId = memberId;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setRank(Rank rank) throws SQLException {
        this.rank = rank;
        Database.members.update(this);
    }
}