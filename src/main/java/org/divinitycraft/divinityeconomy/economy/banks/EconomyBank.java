package org.divinitycraft.divinityeconomy.economy.banks;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.economy.EconomyObject;
import org.divinitycraft.divinityeconomy.economy.FileKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EconomyBank extends EconomyObject {
    Set<FileKey> keys = Set.of(FileKey.BALANCE, FileKey.NAME, FileKey.UUID, FileKey.MEMBERS);
    public EconomyBank(DEPlugin main, File file) {
        super(main, file);
        this.clean();
        this.save();
    }



    /**
     * Returns if the given player is the owner of the bank
     * @param playerName
     * @return
     */
    public boolean isOwner(String playerName) {
        String ownerName = this.getOwnerName();
        if (ownerName == null) return false;
        return ownerName.equalsIgnoreCase(playerName);
    }

    /**
     * Returns if the given player is the owner of the bank
     * @param playerUUID
     * @return
     */
    public boolean isOwner(UUID playerUUID) {
        String ownerUUID = this.getUUIDAsString();
        if (ownerUUID == null) return false;
        return ownerUUID.equalsIgnoreCase(playerUUID.toString());
    }

    /**
     * Returns the owner of the bank
     * @return
     */
    @Nullable
    public String getOwnerName() {
        return getMain().getPlayMan().getPlayerName((getMain().getPlayMan().getPlayer(this.getUUID()))).getName();
    }


    /**
     * Returns the members of the bank
     * @return
     */
    @Nonnull
    public Set<UUID> getMembers() {
        return new HashSet<>(this.config.getStringList(FileKey.MEMBERS.getKey()).stream().map(UUID::fromString).toList());
    }


    /**
     * Adds the given members to the bank
     * @param members
     * @return
     */
    @Nonnull
    public EconomyBank addMembers(@Nonnull Set<UUID> members) {
        Set<UUID> membersSet = this.getMembers();
        membersSet.addAll(members);
        return this.setMembers(membersSet);
    }


    /**
     * Removes the given members from the bank
     * @param members
     * @return
     */
    @Nonnull
    public EconomyBank removeMembers(@Nonnull Set<UUID> members) {
        Set<UUID> membersSet = this.getMembers();
        members.forEach(membersSet::remove);
        return this.setMembers(membersSet);
    }


    /**
     * Returns if the given player is a member of the bank
     * @param playerUUID
     * @return
     */
    public boolean isMember(@Nonnull UUID playerUUID) {
        return this.getMembers().contains(playerUUID);
    }


    /**
     * Sets the members of the bank
     * @param members
     * @return
     */
    public EconomyBank setMembers(Set<UUID> members) {
        this.setAndSave(FileKey.MEMBERS, members);
        return this;
    }

    @Override
    public boolean checkKey(@Nonnull String key) {
        return this.keys.contains(FileKey.get(key));
    }

    /**
     * Deletes the bank
     */
    public void delete() {
        // Delete the file
        File file = this.getFile();
        if (file != null && file.exists()) {
            file.delete();
        }

        // Delete the backup file
        File backupFile = this.getBackupFile();
        if (backupFile != null && backupFile.exists()) {
            backupFile.delete();
        }
    }
}
