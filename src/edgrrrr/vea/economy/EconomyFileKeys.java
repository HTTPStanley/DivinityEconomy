package edgrrrr.vea.economy;

public enum EconomyFileKeys {
    // Keys
    BALANCE("balance"),
    UUID("uuid"),
    NAME("last-known-name"),
    ;
    //END
    private final String key;
    EconomyFileKeys(String key) {
        this.key = key;
    }

    public String get() {
        return this.key;
    }

}
