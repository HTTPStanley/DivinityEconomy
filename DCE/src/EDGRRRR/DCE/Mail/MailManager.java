package EDGRRRR.DCE.Mail;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Calendar;
import java.util.HashMap;

public class MailManager {
    private final DCEPlugin app;
    // The mail file
    private final String mailFile = "mail.yml";
    // Where mail is stored against the player
    private HashMap<OfflinePlayer, MailList> mailMap;

    // Variables for the yaml keys
    private final String strAmount = "amount";
    private final String strBalance = "balance";
    private final String strDate = "date";
    private final String strMessage = "message";
    private final String strSource = "from";
    private final String strRead = "read";


    /**
     * Constructor
     * Use loadAllMail() after constructor for setup and reading of mail.
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
        FileConfiguration mailFile = readMailFile();
        // Create the mail map
        this.mailMap = new HashMap<>();
        // Counters for useful messaging
        int count = 0;
        int users = 0;

        // Loop through users in mail file
        for (String stringUUID : mailFile.getKeys(false)) {
            // Get player the mail belongs to
            // Create their maillist
            // Loop through their mail in the mail file
            OfflinePlayer player = this.app.getPlayerManager().getOfflinePlayerByUUID(stringUUID, true);
            MailList mailList = this.addPlayer(player);
            ConfigurationSection playerMail = mailFile.getConfigurationSection(stringUUID);

            // Creates all mail and stores in the mail list
            for (String mailID : playerMail.getKeys(false)) {
                ConfigurationSection thisMail = playerMail.getConfigurationSection(mailID);
                double amount = thisMail.getDouble(this.strAmount);
                double balance = thisMail.getDouble(this.strBalance);
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(thisMail.getLong(this.strDate));
                String message = thisMail.getString(this.strMessage);
                boolean read = thisMail.getBoolean(this.strRead);
                OfflinePlayer moneyFrom = this.app.getPlayerManager().getOfflinePlayerByUUID(thisMail.getString(this.strSource), true);
                Mail mail = new Mail(message, date, amount, balance, moneyFrom, read);
                mailList.setMail(mailID, mail);
                count += 1;
            }

            // Save mail list for player
            this.addMailList(player, mailList);
            users += 1;
        }
        // Done :)
        this.app.getConsoleManager().info("Loaded " + count + " mail for " + users + " players");
    }

    /**
     * Adds a mail list and stores it against the player
     * @param player - The player to store the mail list for
     * @param mailList - The mail list to store
     */
    public void addMailList(OfflinePlayer player, MailList mailList) {
        this.mailMap.put(player, mailList);
    }

    /**
     * Creates an empty mail list for a player
     * @param player - The player to store the mail for
     */
    public MailList addPlayer(OfflinePlayer player) {
        MailList mailList = new MailList(player);
        this.addMailList(player, mailList);
        return mailList;
    }

    /**
     * Returns a players mail list
     * If the user does not have one, it will create an empty mail list and return it.
     * @param player - The player to get the mail list for
     * @return MailList - The mail list for this player
     */
    public MailList getMailList(OfflinePlayer player) {
        if (!(this.mailMap.containsKey(player))) {
            this.addPlayer(player);
        }
        return this.mailMap.get(player);
    }

    /**
     * Saves all players mail lists to the mail file
     */
    public void saveMail() {
        FileConfiguration mailFile = this.app.getConfigManager().loadConfig(this.mailFile);
        int count = 0;
        int users = 0;
        for (OfflinePlayer player : this.mailMap.keySet()) {
            ConfigurationSection mailListSection = mailFile.createSection(player.getUniqueId().toString());
            MailList mailList = this.mailMap.get(player);

            for (String mailID : mailList.getMailIDs()) {
                Mail mail = mailList.getMail(mailID);
                ConfigurationSection mailSection = mailListSection.createSection(mailID);
                mailSection.set(this.strAmount, mail.getAmount());
                mailSection.set(this.strMessage, mail.getMessage());
                mailSection.set(this.strSource, mail.getSource().getUniqueId().toString());
                mailSection.set(this.strDate, mail.getDateFrom().getTimeInMillis());
                mailSection.set(this.strBalance, mail.getNewBalance());
                mailSection.set(this.strRead, mail.getRead());
                count += 1;
            }
            users += 1;
        }

        this.app.getConfigManager().saveFile(mailFile, this.mailFile);
        this.app.getConsoleManager().info("Saved " + count + " mail for " + users + " players");
    }
}
