package EDGRRRR.DCE.Mail;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class MailList {
    private MailManager manager;
    // Variables for the dictionary keys
    public final String strAmount = "amount";
    public final String strBalance = "balance";
    public final String strDate = "date";
    public final String strMessage = "message";
    public final String strSource = "source";
    public final String strRead = "read";
    // Where the mail is stored
    private final HashMap<String, Mail> mail;
    private final ConfigurationSection configurationSection;
    // The player this mail list belongs to
    private final OfflinePlayer player;

    /**
     * Constructor
     *
     * @param player - The player this mail list belongs to
     */
    public MailList(MailManager manager, OfflinePlayer player, ConfigurationSection configurationSection) {
        this.manager = manager;
        this.player = player;
        this.configurationSection = configurationSection;
        this.mail = new HashMap<>();

        for (String mailID : this.configurationSection.getKeys(false)) {
            ConfigurationSection mailSection = this.configurationSection.getConfigurationSection(mailID);
            Mail mail = new Mail(this, mailSection);
            this.addMail(mail);
        }
    }

    public HashMap<Integer, Mail[]> getPages(int pageSize) {
        HashMap<Integer, Mail[]> pages = new HashMap<>();
        Object[] allMail = this.getMailIDs().toArray();
        int mailCount = allMail.length;

        int pageNumber = 0;
        int pageIdx = 0;
        Mail[] page = new Mail[pageSize];
        for (int mailNumber=0; mailNumber < mailCount; mailNumber++) {
            if (pageIdx == pageSize) {
                pages.put(pageNumber, page);
                pageNumber += 1;
                page = new Mail[pageSize];
            }
            page[pageIdx] = this.getMail((String) allMail[(pageNumber * pageSize) + pageIdx]);
            pageIdx += 1;
        }

        if (!pages.containsValue(page)) {
            pages.put(pageNumber, page);
        }

        return pages;
    }

    public Mail[] getPage(int pageSize, int pageNumber) {
        return this.getPages(pageSize).get(pageNumber);
    }

    /**
     * Returns all mail IDs for which the mail has been read.
     *
     * @return ArrayList<String> - A list of mailIDS that have been read.
     */
    public ArrayList<String> getReadMail() {
        ArrayList<String> readMail = new ArrayList<>();
        for (String mailID : getMailIDs()) {
            if (getMail(mailID).getRead()) {
                readMail.add(mailID);
            }
        }

        return readMail;
    }

    /**
     * Returns all mail IDs for which the mail has not been read
     *
     * @return ArrayList<String> - A list of mailIDs that have not been read
     */
    public ArrayList<String> getUnreadMail() {
        ArrayList<String> unreadMail = new ArrayList<>();
        for (String mailID : getMailIDs()) {
            if (!(getMail(mailID).getRead())) {
                unreadMail.add(mailID);
            }
        }

        return unreadMail;
    }

    /**
     * Returns the mail that corresponds to the passed ID
     * If the ID does not exist, will return null
     *
     * @param mailID - The mail id to get
     * @return Mail - The mail that belongs to this ID
     */
    public Mail getMail(String mailID) {
        return this.mail.get(mailID);
    }

    /**
     * Returns all the mail ids within this mail list
     *
     * @return Set<String> - A list of mail ids
     */
    public Set<String> getMailIDs() {
        return this.mail.keySet();
    }

    /**
     * Returns the internal mail storage
     *
     * @return HashMap<String, Mail> - The internal mail storage
     */
    public HashMap<String, Mail> getAllMail() {
        return this.mail;
    }

    /**
     * Returns the player this mail list belongs to
     *
     * @return OfflinePlayer - The player this mail list belongs to
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Returns true if the number of mail is greater than 0.
     * Else false.
     *
     * @return boolean - If the user has mail
     */
    public boolean hasMail() {
        return (this.getMailIDs().size() > 0);
    }

    /**
     * Returns true if the number of unread mail is greater than 0.
     * Else false.
     *
     * @return boolean - If the user has unread mail
     */
    public boolean hasNewMail() {
        return (this.getUnreadMail().size() > 0);
    }

    /**
     * Adds a mail object to the internal mail storage.
     *
     * @param mail - The mail to add
     * @return mailID - The ID of the mail added
     */
    public String addMail(Mail mail) {
        String mailID = mail.getID();
        this.mail.put(mailID, mail);
        return mailID;
    }

    public String addMail(int amount, double balance, String message, Calendar date, String sourceUUID, boolean read) {
        Mail mail = this.createMail(amount, balance, message, date, sourceUUID, read);
        return mail.getID();
    }

    public ConfigurationSection createMailSection(String name) {
        return this.configurationSection.createSection(name);
    }

    public ConfigurationSection createTempMailSection() {
        return this.createMailSection("temp");
    }

    public void removeTempMailSection() {
        this.setData("temp", null);
    }

    public Mail createMail(double amount, double balance, String message, Calendar date, String sourceUUID, boolean read) {
        ConfigurationSection tempSection = this.createTempMailSection();
        tempSection.set(strAmount, amount);
        tempSection.set(strBalance, balance);
        tempSection.set(strMessage, message);
        tempSection.set(strDate, date.getTimeInMillis());
        tempSection.set(strSource, sourceUUID);
        tempSection.set(strRead, read);
        Mail mail = new Mail(this, tempSection);
        this.addMail(mail);
        this.setData(mail.getID(), mail.getConfigurationSection());
        this.manager.saveMailList(this);
        this.removeTempMailSection();
        return mail;
    }

    /**
     * Puts mail into internal mail storage under given ID
     *
     * @param mailID - The id to store the mail under
     * @param mail   - The mail to store
     */
    public void setMail(String mailID, Mail mail) {
        this.mail.put(mailID, mail);
    }

    /**
     * Removes mail with given id
     *
     * @param mailID - The mailID to remove
     */
    public void removeMail(String mailID) {
        this.mail.remove(mailID);
    }

    /**
     * Removes the given mail from the storage
     *
     * @param mail - The mail to remove
     */
    public void removeMail(Mail mail) {
        for (String mailID : this.mail.keySet()) {
            Mail thisMail = this.mail.get(mailID);
            if (thisMail == mail) {
                this.mail.remove(mailID);
                break;
            }
        }
    }

    public ConfigurationSection getConfigurationSection() {
        return this.configurationSection;
    }

    private void setData(String key, Object value) {
        this.configurationSection.set(key, value);
    }

    public void saveAllMail() {
        for (Mail mail : this.getAllMail().values()) {
            this.setData(mail.getID(), mail.getConfigurationSection());
        }
    }
}
