package me.edgrrrr.de.mail;

import me.edgrrrr.de.utils.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MailList {
    // Variables for the dictionary keys
    public final String strAmount = "amount";
    public final String strBalance = "balance";
    public final String strDate = "date";
    public final String strMessage = "message";
    public final String strSource = "source";
    public final String strRead = "read";
    // Where the mail is stored
    private final Map<String, Mail> mail;
    private final ConfigurationSection configurationSection;
    // The player this mail list belongs to
    private final String playerUUID;

    /**
     * Constructor
     */
    public MailList(String UUID, ConfigurationSection configurationSection) {
        this.playerUUID = UUID;
        this.configurationSection = configurationSection;
        this.mail = new ConcurrentHashMap<>();

        for (String mailID : this.configurationSection.getKeys(false)) {
            ConfigurationSection mailSection = this.configurationSection.getConfigurationSection(mailID);
            if (mailSection == null) {
                continue;
            }
            Mail mail = new Mail(this, mailSection);
            this.addMail(mail);
        }
    }

    /**
     * Returns "pages" of mail.
     *
     * @param pageSize - The size of each page.
     * @return HashMap<Integer, Mail [ ]> - Pages of mail.
     */
    public Map<Integer, Mail[]> getPages(int pageSize) {
        Mail[] allMail = this.getAllMail().values().toArray(new Mail[0]);
        Map<Integer, List<Object>> toPages = ArrayUtils.paginator(allMail, pageSize);
        Map<Integer, Mail[]> mailPages = new ConcurrentHashMap<>();
        for (int pageNum : toPages.keySet()) {
            List<Object> objects = toPages.get(pageNum);
            List<Mail> mail = new ArrayList<>();
            objects.forEach(obj -> mail.add((Mail) obj));
            mailPages.put(pageNum, mail.toArray(new Mail[0]));
        }
        return mailPages;
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
    public Map<String, Mail> getAllMail() {
        return this.mail;
    }

    /**
     * Returns the player this mail list belongs to
     */
    public String getPlayer() {
        return this.playerUUID;
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
        this.setData(mailID, mail.getConfigurationSection());
        return mailID;
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

    public Mail createMail(String message, Calendar date, boolean read) {
        ConfigurationSection tempSection = this.createTempMailSection();
        tempSection.set(strMessage, message);
        tempSection.set(strDate, date.getTimeInMillis());
        tempSection.set(strRead, read);
        Mail mail = new Mail(this, tempSection);
        this.setMail(mail.getID(), mail);
        this.removeTempMailSection();
        return mail;
    }

    public Mail createMail(String message) {
        return this.createMail(message, Calendar.getInstance(), false);
    }

    /**
     * Puts mail into internal mail storage under given ID
     *
     * @param mailID - The id to store the mail under
     * @param mail   - The mail to store
     */
    public void setMail(String mailID, Mail mail) {
        this.mail.put(mailID, mail);
        this.setData(mailID, mail.getConfigurationSection());
    }

    /**
     * Removes mail with given id
     *
     * @param mailID - The mailID to remove
     */
    public void removeMail(String mailID) {
        this.mail.remove(mailID);
        this.setData(mailID, null);
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
                this.removeMail(thisMail.getID());
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
