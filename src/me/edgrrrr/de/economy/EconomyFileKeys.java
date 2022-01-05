package me.edgrrrr.de.economy;

public enum EconomyFileKeys {
    // Keys
    BALANCE("balance"),
    NAME("last-known-name"),
    LOGS("logs")
    ;
    //END
    public final String key;

    EconomyFileKeys(String key) {
        this.key = key;
    }
}
