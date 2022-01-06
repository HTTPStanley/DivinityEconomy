package me.edgrrrr.de.economy;

public enum EconomyFileKeys {
    // Keys
    BALANCE("balance"),
    NAME("last-known-name"),
    UUID("uuid"),
    LOGS("logs")
    ;
    //END
    public final String key;

    EconomyFileKeys(String key) {
        this.key = key;
    }
}
