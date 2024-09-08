package me.edgrrrr.de.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.mail.events.MailEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MailManager extends DivinityModule {
    // The mail file
    private final String mailFile = "mail.yml";
    // Where mail is stored against the player
    private Map<String, MailList> mailMap;
    // Where the config is stored
    private FileConfiguration configuration;

    private boolean enableMail;
    private boolean enableMailNotify;
    private boolean enableMailNotifySilent;


    /**
     * Constructor
     * Use loadAllMail() after constructor for setup and reading of mail.
     *
     * @param main - The java plugin
     */
    public MailManager(DEPlugin main) {
        super(main);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        // Get settings
        this.enableMail = getMain().getConfMan().getBoolean(Setting.MAIL_ENABLE_BOOLEAN);
        this.enableMailNotify = getMain().getConfMan().getBoolean(Setting.MAIL_NOTIFY_BOOLEAN);
        this.enableMailNotifySilent = getMain().getConfMan().getBoolean(Setting.MAIL_NOTIFY_SILENT_BOOLEAN);

        // Load mail
        if (this.enableMail) {
            this.setupMailFile();
            this.loadAllMail();
        }

        // Create tasks
        if (this.enableMailNotify && this.enableMail) {
            getMain().getServer().getPluginManager().registerEvents(new MailEvent(getMain(), enableMailNotifySilent), getMain());
        }
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        if (this.enableMail) {
            this.saveAllMail();
        }
    }

    /**
     * Returns if mail is enabled
     *
     * @return boolean - If mail is enabled
     */
    public boolean isMailEnabled() {
        return enableMail;
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
        return this.getConfMan().loadFile(this.mailFile);
    }

    /**
     * Loads all mail into the class
     * Mail cannot be read unless this is called!
     */
    public void loadAllMail() {
        this.configuration = this.getConfMan().loadFile(this.mailFile);
        this.mailMap = new ConcurrentHashMap<>();
        int userCount = 0;
        int mailCount = 0;
        for (String userID : this.configuration.getKeys(false)) {
            ConfigurationSection mailListSection = this.configuration.getConfigurationSection(userID);
            MailList mailList = new MailList(userID, mailListSection);
            this.addMailList(userID, mailList);
            userCount += 1;
            mailCount += mailList.getMailIDs().size();
        }

        this.getConsole().info(LangEntry.MAIL_MailLoaded.get(getMain()), mailCount, userCount);
    }

    /**
     * Adds a mail list and stores it against the player
     *
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

    /**
     * Creates a mail list section in the mail file
     *
     * @param uuid - The player to create the mail list for
     * @return ConfigurationSection - The mail list section
     */
    public ConfigurationSection createMailListSection(String uuid) {
        return this.configuration.createSection(uuid);
    }

    /**
     * Returns a players mail list
     * If the user does not have one, it will create an empty mail list and return it.
     *
     * @return MailList - The mail list for this player
     */
    public MailList getMailList(String uuid) {
        // If mail system is disabled, return dummy mail list
        if (!this.enableMail) {
            return new MailList(uuid, null);
        }

        // If the player does not have a mail list, create one
        if (!(this.mailMap.containsKey(uuid))) {
            this.addPlayer(uuid);
        }

        // Return the mail list
        return this.mailMap.get(uuid);
    }

    /**
     * Sets data in the mail file
     *
     * @param key   - The key to set
     * @param value - The value to set
     */
    private void setData(String key, Object value) {
        this.configuration.set(key, value);
    }

    /**
     * Saves a mail list to the mail file
     *
     * @param mailList - The mail list to save
     */
    public void saveMailList(MailList mailList) {
        mailList.saveAllMail();
        this.setData(mailList.getPlayer(), mailList.getConfigurationSection());
    }

    /**
     * Saves the mail file
     */
    private void saveMailFile() {
        this.getConfMan().saveFile(this.configuration, this.mailFile);
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
        this.getConsole().info(LangEntry.MAIL_MailSaved.get(getMain()), mailCount, userCount);
    }
}
