package EDGRRRR.DCE.Mail;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MailList {
    // Where the mail is stored
    private HashMap<String, Mail> mail;

    // The player this mail list belongs to
    private OfflinePlayer player;

    /**
     * Constructor
     * @param player - The player this mail list belongs to
     */
    public MailList(OfflinePlayer player) {
        this.player = player;
        this.mail = new HashMap<>();
    }

    /**
     * Returns all mail IDs for which the mail has been read.
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
     * @param mailID - The mail id to get
     * @return Mail - The mail that belongs to this ID
     */
    public Mail getMail(String mailID) {
        return this.mail.get(mailID);
    }

    /**
     * Returns all the mail ids within this mail list
     * @return Set<String> - A list of mail ids
     */
    public Set<String> getMailIDs() {
        return this.mail.keySet();
    }

    /**
     * Returns the internal mail storage
     * @return HashMap<String, Mail> - The internal mail storage
     */
    public HashMap<String, Mail> getAllMail() {
        return this.mail;
    }

    /**
     * Returns the player this mail list belongs to
     * @return OfflinePlayer - The player this mail list belongs to
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Returns true if the number of mail is greater than 0.
     * Else false.
     * @return boolean - If the user has mail
     */
    public boolean hasMail() {
        return (this.getMailIDs().size() > 0);
    }

    /**
     * Returns true if the number of unread mail is greater than 0.
     * Else false.
     * @return boolean - If the user has unread mail
     */
    public boolean hasNewMail() {
        return (this.getUnreadMail().size() > 0);
    }

    /**
     * Adds a mail object to the internal mail storage.
     * @param mail - The mail to add
     * @return mailID - The ID of the mail added
     */
    public String addMail(Mail mail) {
        String mailID = mail.getID();
        this.mail.put(mailID, mail);
        return mailID;
    }

    /**
     * Puts mail into internal mail storage under given ID
     * @param mailID - The id to store the mail under
     * @param mail - The mail to store
     */
    public void setMail(String mailID, Mail mail) {
        this.mail.put(mailID, mail);
    }

    /**
     * Removes mail with given id
     * @param mailID - The mailID to remove
     */
    public void removeMail(String mailID) {
        this.mail.remove(mailID);
    }

    /**
     * Removes the given mail from the storage
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
}
