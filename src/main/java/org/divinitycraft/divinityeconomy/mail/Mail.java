package org.divinitycraft.divinityeconomy.mail;

import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class representing ingame mail.
 * Message variables:
 * <daysAgo> - The days since event
 * <hoursAgo> - The hours since the event
 * <minsAgo> - The minutes since the event
 * <aptime> - The appropriate time since the event
 */
public class Mail {
    private final MailList mailList;
    private final ConfigurationSection configurationSection;

    /**
     * Constructor
     *
     * @param configurationSection
     * @param mailList
     */
    public Mail(MailList mailList, ConfigurationSection configurationSection) {
        this.mailList = mailList;
        this.configurationSection = configurationSection;
    }

    /**
     * Returns the Unique ID of the mail
     *
     * @return String - Unique ID of mail
     */
    public String getID() {
        return String.valueOf(this.getDateFrom().getTimeInMillis());
    }

    /**
     * Returns the message of this mail
     *
     * @return String - The message of the mail
     */
    public String getRawMessage() {
        return this.configurationSection.getString(this.mailList.strMessage);
    }

    public String getMessage() {
        String message = this.getRawMessage();
        message = message.replace("<daysAgo>", String.valueOf(this.getDaysSince()));
        message = message.replace("<hoursAgo>", String.valueOf(this.getHoursSince()));
        message = message.replace("<minsAgo>", String.valueOf(this.getMinutesSince()));
        Map<String, Object> aptime = this.getApTimeSince();
        message = message.replace("<aptime>", String.format("%s %s ago", aptime.get("value"), aptime.get("type")));
        return message;
    }

    /**
     * Returns the date from when the mail was created
     *
     * @return Calendar - The creation date of the mail
     */
    public Calendar getDateFrom() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.configurationSection.getLong(this.mailList.strDate));
        return date;
    }

    /**
     * Returns the time delta between the creation date and now
     *
     * @return Duration - The time delta
     */
    public Duration getTimeSince() {
        Calendar currentDate = Calendar.getInstance();
        return Duration.between(this.getDateFrom().toInstant(), currentDate.toInstant());
    }

    /**
     * Returns the number of days between the creation date and now
     *
     * @return int - The number of days
     */
    public int getDaysSince() {
        return (int) this.getTimeSince().toDays();
    }

    /**
     * Returns the number of hours between the creation date and now
     *
     * @return int
     */
    public int getHoursSince() {
        return (int) this.getTimeSince().toHours();
    }

    /**
     * Returns the number of minutes between the creation date and now
     *
     * @return int
     */
    public int getMinutesSince() {
        return (int) this.getTimeSince().toMinutes();
    }

    /**
     * Returns the appropriate time since the creation date.
     *
     * @return String, Integer - The type, the value
     */
    public Map<String, Object> getApTimeSince() {
        int days = this.getDaysSince();
        int hours = this.getHoursSince();
        int minutes = this.getMinutesSince();

        Map<String, Object> result = new ConcurrentHashMap<>();

        if (minutes <= 120) {
            result.put("value", minutes);
            result.put("type", "minutes");
        } else if (hours <= 72) {
            result.put("value", hours);
            result.put("type", "hours");
        } else {
            result.put("value", days);
            result.put("type", "days");
        }

        return result;
    }

    /**
     * Returns if this mail has been read or not
     *
     * @return boolean - is read
     */
    public boolean getRead() {
        return this.configurationSection.getBoolean(this.mailList.strRead);
    }

    /**
     * Sets the read state of the mail
     *
     * @param state - The read state
     */
    public void setRead(boolean state) {
        this.configurationSection.set(this.mailList.strRead, state);
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }
}
