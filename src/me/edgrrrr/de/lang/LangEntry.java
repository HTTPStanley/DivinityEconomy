package me.edgrrrr.de.lang;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.console.LogLevel;

import java.util.List;

public enum LangEntry {
    // Generic Command Responses
    GENERIC_PluginEnabled(),
    GENERIC_PluginDisabled(),
    GENERIC_EconomyNotEnabled(LogLevel.SEVERE),
    GENERIC_EconomyEnabled(),
    GENERIC_PAPIEnabled(),
    GENERIC_PAPINotEnabled(LogLevel.WARNING),
    GENERIC_PAPINotFound(LogLevel.WARNING),
    GENERIC_ConsoleCommandIsDisabled(LogLevel.WARNING),
    GENERIC_ConsoleSupportNotAdded(LogLevel.WARNING),
    GENERIC_PlayerCommandIsDisabled(LogLevel.WARNING),
    GENERIC_ErrorOnCommand(LogLevel.SEVERE),
    GENERIC_InvalidNumberOfArguments(LogLevel.WARNING),
    GENERIC_InvalidArguments(LogLevel.WARNING),
    GENERIC_InvalidPlayerName(LogLevel.WARNING),
    GENERIC_InvalidAmountGiven(LogLevel.WARNING),
    GENERIC_UnknownError(LogLevel.WARNING),
    GENERIC_IncorrectCommandUsage(LogLevel.WARNING),
    GENERIC_CommandUsage(),
    GENERIC_HelpFor(),
    GENERIC_Description(),
    GENERIC_Usages(),
    GENERIC_Aliases(),
    GENERIC_FileSaved(),
    GENERIC_LocaleError(LogLevel.WARNING),
    GENERIC_PlayerNoPermission(LogLevel.WARNING),


    // Describe
    DESCRIBE_Header(),
    DESCRIBE_Settings(),
    DESCRIBE_Markets(),
    DESCRIBE_Materials(),
    DESCRIBE_MaterialMarketSize(),
    DESCRIBE_MaterialMarketInflation(),
    DESCRIBE_Enchants(),
    DESCRIBE_EnchantMarketSize(),
    DESCRIBE_EnchantMarketInflation(),
    DESCRIBE_Experience(),
    DESCRIBE_ExperienceMarketSize(),
    DESCRIBE_ExperienceMarketInflation(),
    DESCRIBE_Entities(),
    DESCRIBE_EntitiesMarketSize(),
    DESCRIBE_EntitiesMarketInflation(),
    DESCRIBE_Potions(),
    DESCRIBE_PotionsMarketSize(),
    DESCRIBE_PotionsMarketInflation(),


    // Reload
    RELOAD_Generic(),
    RELOAD_Enchants(),
    RELOAD_Materials(),
    RELOAD_Experience(),
    RELOAD_Config(),
    RELOAD_Potions(),
    RELOAD_Entities(),
    RELOAD_TypeRequired(),


    // Save
    SAVE_Config(),
    SAVE_Enchants(),
    SAVE_Materials(),
    SAVE_Experience(),
    SAVE_Potions(),
    SAVE_Entities(),
    SAVE_TypeRequired(),

    // Baltop
    BALTOP_NothingToDisplay(),
    BALTOP_LastOrderedAt(),
    BALTOP_ServerTotal(),
    BALTOP_YourPositionIs(),


    // Ping
    PING_PingResponse(),


    // MISC
    MISC_EnableNotifications(),
    MISC_EnableNotificationsFor(),


    // Balance
    BALANCE_Response(),
    BALANCE_ResponseOther(),
    BALANCE_ChangedLog(),
    BALANCE_ChangeFailedLog(),
    BALANCE_ChangedSourcePlayer(),
    BALANCE_ChangedTargetPlayer(),
    BALANCE_ClearedByPlayer(),
    BALANCE_ClearedByConsole(),
    BALANCE_ChangedByPlayer(),
    BALANCE_ChangedByConsole(),
    BALANCE_SetByPlayer(),
    BALANCE_SetByConsole(),


    // Purchase
    PURCHASE_Log(),
    PURCHASE_FailedLog(),
    PURCHASE_Response(),
    PURCHASE_FailedResponse(),
    PURCHASE_ValueEnchantFailure(),
    PURCHASE_ValueEnchantSummary(),
    PURCHASE_ValueEnchant(),


    // Sell
    SALE_Log(),
    SALE_FailedLog(),
    SALE_Response(),
    SALE_FailedResponse(),
    SALE_ValueEnchantFailure(),
    SALE_ValueEnchantSummary(),
    SALE_ValueEnchant(),


    // Value
    VALUE_BuyResponse(),
    VALUE_SellResponse(),
    VALUE_BuyFailedResponse(),
    VALUE_SellFailedResponse(),
    VALUE_Response(),


    // Transfer
    TRANSFER_Log(),
    TRANSFER_FailedLog(),
    TRANSFER_SourceResponse(),
    TRANSFER_TargetResponse(),
    TRANSFER_FailedResponse(),


    // Economy
    ECONOMY_NegativeAmounts(),
    ECONOMY_Overdraft(),
    ECONOMY_OverLimit(),
    ECONOMY_BankNotExist(),
    ECONOMY_PlayerNotExist(),
    ECONOMY_BankAlreadyExists(),
    ECONOMY_CreatedBank(),
    ECONOMY_DeletedBank(),
    ECONOMY_BankBalance(),
    ECONOMY_BankHas(),
    ECONOMY_BankWithdraw(),
    ECONOMY_BankDeposit(),
    ECONOMY_IsOwner(),
    ECONOMY_IsMember(),
    ECONOMY_IncorrectlyFormattedBalance(),
    ECONOMY_RecoveredFile(),
    ECONOMY_FailedToRecoverFile(),
    ECONOMY_FailedToCreateBankFile(),
    ECONOMY_CreatingPlayerFile(),
    ECONOMY_Prefix(),
    ECONOMY_Suffix(),


    // Help
    HELP_Header(),
    HELP_NoneFound(),
    HELP_Command(),
    HELP_Term(),
    HELP_Page(),
    HELP_NullEntry(),
    HELP_EntryError(),
    HELP_HelpLoaded(),


    // Mail
    MAIL_NothingToClear(),
    MAIL_Removed(),
    MAIL_InvalidPage(),
    MAIL_YouHaveNoMail(),
    MAIL_InvalidPageChoose(),
    MAIL_List(),
    MAIL_MailLoaded(),
    MAIL_MailSaved(),
    MAIL_MailNotification(),
    MAIL_NoMailNotification(),


    // Stock Changed
    STOCK_CountChanged(),
    STOCK_ValueChanged(),
    STOCK_BannedStatusChanged(),


    // Worlds
    WORLDS_BothEnabled(),
    WORLDS_BothDisabled(),
    WORLDS_MarketEnabled(),
    WORLDS_EconomyEnabled(),
    WORLDS_EconomyDisabledInThisWorld(),
    WORLDS_MarketDisabledInThisWorld(),
    WORLDS_ItemMarketDisabledInThisWorld(),
    WORLDS_EnchantMarketDisabledInThisWorld(),
    WORLDS_ExperienceMarketDisabledInThisWorld(),


    // Market
    MARKET_UnknownMaterial(LogLevel.WARNING),
    MARKET_UnknownItem(LogLevel.WARNING),
    MARKET_UnknownEnchant(LogLevel.WARNING),
    MARKET_InvalidItemName(LogLevel.WARNING),
    MARKET_InvalidMaterialName(LogLevel.WARNING),
    MARKET_InvalidEnchantName(LogLevel.WARNING),
    MARKET_InvalidItemHeld(LogLevel.WARNING),
    MARKET_InvalidInventorySpace(LogLevel.WARNING),
    MARKET_InvalidStockAmount(LogLevel.WARNING),
    MARKET_InvalidInventoryStock(LogLevel.WARNING),
    MARKET_EnchantsInvalidItemAmount(LogLevel.WARNING),
    MARKET_NothingToSellAfterSkipping(LogLevel.WARNING),
    MARKET_NothingToSell(LogLevel.WARNING),
    MARKET_EnchantList(),
    MARKET_MaterialMarketIsDisabled(LogLevel.WARNING),
    MARKET_EnchantMarketIsDisabled(LogLevel.WARNING),
    MARKET_ExperienceMarketIsDisabled(LogLevel.WARNING),
    MARKET_YouAreNotHoldingAnItem(),
    MARKET_NoItemsToSell(),
    MARKET_NoItemsToBuy(),
    MARKET_ItemCannotBeFound(),
    MARKET_ItemIsBanned(),
    MARKET_ItemIsWorthless(),
    MARKET_ItemIsEnchanted(),
    MARKET_ItemIsNamedOrLored(),
    MARKET_ItemIsNotSupported(),
    MARKET_ItemIsNotSupportedOnThisItem(),
    MARKET_LevelIsGreaterThanMax(),
    MARKET_LevelWouldBeGreaterThanMax(),
    MARKET_ItemDoesNotExist(),
    MARKET_ItemDoesNotExistInTheStore(),
    MARKET_ItemIsNotEnchanted(),
    MARKET_ItemNotEnoughLevels(),
    MARKET_ItemAliasesLoaded(),
    MARKET_ItemsLoaded(),


    // Experience
    EXPERIENCE_InvalidAmount(),
    EXPERIENCE_IsBanned(),
    EXPERIENCE_NotEnoughExperienceToBuy(),
    EXPERIENCE_NotEnoughExperienceToSell(),
    EXPERIENCE_IsWorthless(),



    // Info
    INFO_InformationFor(),
    INFO_TypeInformation(),
    INFO_IDInformation(),
    INFO_CurrentQuantityInformation(),
    INFO_IsBannedInformation(),



    // Item List
    ITEMLIST_Header(),
    ITEMLIST_AscendingOrder(),
    ITEMLIST_OrderBy(),


    // SellAll
    SELLALL_Whitelist(),
    SELLALL_Blacklist(),
    SELLALL_Empty(),


    // Words
    W_max(),
    W_all(),
    W_read(),
    W_unread(),
    W_unknown(),
    W_name(),
    W_pagenumber(),
    W_items(),
    W_enchants(),
    W_experience(),
    W_price(),
    W_stock(),
    W_alphabet(),
    W_config(),
    W_materials(),
    W_potions(),
    W_entities(),
    W_empty(),


    ;
    // Fields
    public final String path;
    public final LogLevel logLevel;


    // Constructors
    LangEntry() {
        this.path = this.name();
        this.logLevel = LogLevel.INFO;
    }

    LangEntry(String path) {
        this.path = path;
        this.logLevel = LogLevel.INFO;
    }

    LangEntry(LogLevel logLevel) {
        this.path = this.name();
        this.logLevel = logLevel;
    }


    LangEntry(String path, LogLevel logLevel) {
        this.path = path;
        this.logLevel = logLevel;
    }


    /**
     * A hacky way to get the language entry from the plugin
     * @param plugin
     * @return
     */
    public String get(DEPlugin plugin) {
        return plugin.getLang().get(this);
    }


    /**
     * A hacky way to get the language entry from the plugin
     * @param plugin
     * @param args
     * @return
     */
    public String get(DEPlugin plugin, Object... args) {
        return plugin.getLang().get(this, args);
    }


    /**
     * A hacky way to get the language entry from the plugin
     * @param plugin
     * @return
     */
    public String getDefault(DEPlugin plugin) {
        return plugin.getLang().get(this);
    }


    /**
     * A hacky way to get the language entry from the plugin
     * @param plugin
     * @param args
     * @return
     */
    public String getDefault(DEPlugin plugin, Object... args) {
        return plugin.getLang().get(this, args);
    }


    /**
     * A hacky way to add the default and current language to the list
     * @param plugin
     * @param list
     */
    public void addLang(DEPlugin plugin, List<String> list) {
        String lang = plugin.getLang().get(this);
        String defaultLang = plugin.getLang().getDefault(this);

        list.add(defaultLang);
        if (!lang.equals(defaultLang)) {
            list.add(lang);
        }
    }


    /**
     * A hacky way to check if the language entry is the same as a given string
     */
    public boolean is(DEPlugin plugin, String string) {
        return plugin.getLang().get(this).equalsIgnoreCase(string) || plugin.getLang().getDefault(this).equalsIgnoreCase(string);
    }
}
