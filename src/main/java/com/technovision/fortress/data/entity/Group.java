package com.technovision.fortress.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.technovision.fortress.data.Database;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Entity(name = "groups")
public class Group {

    @DatabaseField(id = true, unique = true)
    private String name;

    @DatabaseField
    private UUID ownerID;

    @DatabaseField
    private boolean isPublic;

    @DatabaseField
    private String password;

    @DatabaseField
    private Date dateCreated;

    @ForeignCollectionField(eager = true)
    private Collection<Member> members;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rank memberRank;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rank moderatorRank;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rank adminRank;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rank ownerRank;

    @DatabaseField
    private String biography;

    public Group() { }

    public Group(String name, UUID playerId) throws SQLException {
        new Group(name, playerId, false, null);
    }

    public Group(String name, UUID playerId, boolean isPublic) throws SQLException {
        new Group(name, playerId, isPublic, null);
    }

    public Group(String name, UUID playerId, boolean isPublic, String password) throws SQLException {
        this.name = name;
        this.biography = null;
        this.ownerID = playerId;
        this.isPublic = isPublic;
        this.password = password;
        this.dateCreated = new Date(System.currentTimeMillis());

        // Create ranks
        this.memberRank = new Rank("member", this);
        Database.ranks.create(memberRank);
        this.moderatorRank = new Rank("moderator", this);
        Database.ranks.create(moderatorRank);
        this.adminRank = new Rank("admin", this);
        Database.ranks.create(adminRank);
        this.ownerRank = new Rank("owner", this);
        Database.ranks.create(ownerRank);

        // Create owner
        Member owner = new Member(ownerID, this, ownerRank);
        Database.members.create(owner);
        this.members = new HashSet<>();
    }

    public void invitePlayer(UUID playerId) throws SQLException {
        Member invitedMember = new Member(playerId, this, true);
        Database.members.create(invitedMember);
    }

    public void removePlayer(Member member) throws SQLException {
        Database.members.delete(member);
    }

    public void acceptPlayer(Member member) throws SQLException {
        member.setRank(memberRank);
        Database.members.createOrUpdate(member);
    }

    public void delete() throws SQLException {
        DeleteBuilder<Rank, Integer> rankBuilder = Database.ranks.deleteBuilder();
        rankBuilder.where().eq("group_id", name);
        rankBuilder.delete();

        DeleteBuilder<Member, Integer> memBuilder = Database.members.deleteBuilder();
        memBuilder.where().eq("group_id", name);
        memBuilder.delete();

        Database.groups.delete(this);
    }

    /** Getters */

    public Collection<Member> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getPassword() {
        return password;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Rank getMemberRank() {
        return memberRank;
    }

    public Rank getModeratorRank() {
        return moderatorRank;
    }

    public Rank getAdminRank() {
        return adminRank;
    }

    public Rank getOwnerRank() {
        return ownerRank;
    }


    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setBiography(String biography) throws SQLException {
        this.biography = biography;
        Database.groups.update(this);
    }

    public void setOwnerID(UUID ownerID) throws SQLException {
        this.ownerID = ownerID;
        Database.groups.update(this);
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
