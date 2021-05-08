package edgrrrr.dce.mail;

import edgrrrr.dce.DCEPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class MailManager {
    private final DCEPlugin app;
    // The mail file
    private final String mailFile = "mail.yml";
    // Where mail is stored against the player
    private HashMap<String, MailList> mailMap;
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
        return this.app.getConfigManager().loadFile(this.mailFile);
    }

    /**
     * Loads all mail into the class
     * Mail cannot be read unless this is called!
     */
    public void loadAllMail() {
        this.configuration = this.app.getConfigManager().loadFile(this.mailFile);
        this.mailMap = new HashMap<>();
        int userCount = 0;
        int mailCount = 0;
        for (String userID : this.configuration.getKeys(false)) {
            ConfigurationSection mailListSection = this.configuration.getConfigurationSection(userID);
            MailList mailList = new MailList(userID, mailListSection);
            this.addMailList(userID, mailList);
            userCount += 1;
            mailCount += mailList.getMailIDs().size();
        }

        this.app.getConsole().info("Read " + mailCount + " mail for " + userCount + " users.");
    }

    /**
     * Adds a mail list and stores it against the player
     * @param mailList - The mail list to store
     */
    public void addMailList(String uuid, MailList mailList) {
        this.mailMap.put(uuid, mailList);
        this.saveMailList(mailList);
    }

    /**
     * Creates an empty mail list for a player
     */
    public MailList addPlayer(String uuid) {
        ConfigurationSection mailSection = this.createMailListSection(uuid);
        MailList mailList = new MailList(uuid, mailSection);
        this.addMailList(uuid, mailList);
        return mailList;
    }

    public ConfigurationSection createMailListSection(String uuid) {
        return this.configuration.createSection(uuid);
    }

    /**
     * Returns a players mail list
     * If the user does not have one, it will create an empty mail list and return it.
     * @return MailList - The mail list for this player
     */
    public MailList getMailList(String uuid) {
        if (!(this.mailMap.containsKey(uuid))) {
            this.addPlayer(uuid);
        }
        return this.mailMap.get(uuid);
    }

    private void setData(String key, Object value) {
        this.configuration.set(key, value);
    }

    public void saveMailList(MailList mailList) {
        mailList.saveAllMail();
        this.setData(mailList.getPlayer(), mailList.getConfigurationSection());
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
            this.saveMailList(mailList);
            userCount += 1;
            mailCount += mailList.getMailIDs().size();
        }

        this.saveMailFile();
        this.app.getConsole().info("Saved " + mailCount + " mail for " + userCount + " users");
    }
}
