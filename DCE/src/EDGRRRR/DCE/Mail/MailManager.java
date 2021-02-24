package EDGRRRR.DCE.Mail;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class MailManager {
    private final DCEPlugin app;
    // The mail file
    private final String mailFile = "mail.yml";
    // Where mail is stored against the player
    private HashMap<OfflinePlayer, MailList> mailMap;
    // Where the config is stored
    private FileConfiguration configuration;


    /**
     * Constructor
     * Use loadAllMail() after constructor for setup and reading of mail.
     *
     * @param app - The java plugin
     */
    public MailManager(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Setup the mail file in the config folder
     */
    public void setupMailFile() {
        this.readMailFile();
    }

    /**
     * Reads and returns the mail file
     *
     * @return FileConfiguration - The contents of the mail file
     */
    public FileConfiguration readMailFile() {
        return this.app.getConfigManager().loadConfig(this.mailFile);
    }

    /**
     * Loads all mail into the class
     * Mail cannot be read unless this is called!
     */
    public void loadAllMail() {
        this.configuration = this.app.getConfigManager().loadConfig(this.mailFile);
        this.mailMap = new HashMap<>();
        int userCount = 0;
        int mailCount = 0;
        for (String userID : this.configuration.getKeys(false)) {
            ConfigurationSection mailListSection = this.configuration.getConfigurationSection(userID);
            OfflinePlayer player = this.app.getPlayerManager().getOfflinePlayerByUUID(userID, true);
            MailList mailList = new MailList(this, player, mailListSection);
            this.addMailList(player, mailList);
            userCount += 1;
            mailCount += mailList.getMailIDs().size();
        }

        this.app.getConsoleManager().info("Read " + mailCount + " mail for " + userCount + " users.");
    }

    /**
     * Adds a mail list and stores it against the player
     *
     * @param player   - The player to store the mail list for
     * @param mailList - The mail list to store
     */
    public void addMailList(OfflinePlayer player, MailList mailList) {
        this.mailMap.put(player, mailList);
    }

    /**
     * Creates an empty mail list for a player
     *
     * @param player - The player to store the mail for
     */
    public MailList addPlayer(OfflinePlayer player) {
        ConfigurationSection mailSection = this.createMailListSection(player);
        MailList mailList = new MailList(this, player, mailSection);
        this.addMailList(player, mailList);
        return mailList;
    }

    public ConfigurationSection createMailListSection(OfflinePlayer player) {
        return this.configuration.createSection(player.getUniqueId().toString());
    }

    /**
     * Returns a players mail list
     * If the user does not have one, it will create an empty mail list and return it.
     *
     * @param player - The player to get the mail list for
     * @return MailList - The mail list for this player
     */
    public MailList getMailList(OfflinePlayer player) {
        if (!(this.mailMap.containsKey(player))) {
            this.addPlayer(player);
        }
        return this.mailMap.get(player);
    }

    private void setData(String key, Object value) {
        this.configuration.set(key, value);
    }

    public void saveMailList(MailList mailList) {
        mailList.saveAllMail();
        this.setData(mailList.getPlayer().getUniqueId().toString(), mailList.getConfigurationSection());
    }

    private void saveMailFile() {
        this.app.getConfigManager().saveFile(this.configuration, this.mailFile);
    }

    /**
     * Saves all players mail lists to the mail file
     */
    public void saveAllMail() {
        int userCount = 0;
        int mailCount = 0;
        for (MailList mailList : this.mailMap.values()) {
            saveMailList(mailList);
            userCount += 1;
            mailCount += mailList.getMailIDs().size();
        }

        this.saveMailFile();
        this.app.getConsoleManager().info("Saved " + mailCount + " mail for " + userCount + " users");
    }
}
