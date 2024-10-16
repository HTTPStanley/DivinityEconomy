package org.divinitycraft.divinityeconomy.config;

public enum Setting {

    /**
     * Values starting with "SECTION" are configuration sections, and do not store
     * a configurable value
     * Values not starting with "SECTION" are individual configurable values and
     * can be set/got, they end in the type they return
     */

    //Setting Sections
    SECTION_MAIN("Main"),
    SECTION_CHAT("Chat"),
    SECTION_ECONOMY("Economy"),
    SECTION_MESSAGES("Message Ignore"),
    SECTION_COMMANDS("Commands"),
    SECTION_MARKET("Market"),
    SECTION_WORLDS("Worlds"),
    SECTION_MAIL("Mail"),

    SECTION_COMMANDS_ADMIN(SECTION_COMMANDS.path + ".Admin"),
    SECTION_COMMANDS_MAIL(SECTION_COMMANDS.path + ".Mail"),
    SECTION_COMMANDS_ECONOMY(SECTION_COMMANDS.path + ".Economy"),
    SECTION_COMMANDS_MISC(SECTION_COMMANDS.path + ".Misc"),
    SECTION_COMMANDS_MARKET(SECTION_COMMANDS.path + ".Market"),
    SECTION_COMMANDS_ENCHANT(SECTION_COMMANDS.path + ".Enchant"),
    SECTION_COMMANDS_EXPERIENCE(SECTION_COMMANDS.path + ".Experience"),

    SECTION_MARKET_MATERIALS(SECTION_MARKET.path + ".Materials"),
    SECTION_MARKET_ENCHANTS(SECTION_MARKET.path + ".Enchants"),

    SECTION_MARKET_EXP(SECTION_MARKET.path + ".Experience"),

    //Main Settings
    MAIN_VERSION_STRING(SECTION_MAIN.path + ".Version"),
    MAIN_ENABLE_PAPI_BOOLEAN(SECTION_MAIN.path + ".Enable PAPI"),
    MAIN_LANG_FILE_STRING(SECTION_MAIN.path + ".Language File"),
    MAIN_TRANSLATE_ITEMS_BOOLEAN(SECTION_MAIN.path + ".Translate Item Names"),
    MAIN_ENABLE_BSTATS_BOOLEAN(SECTION_MAIN.path + ".Enable bStats"),
    MAIN_SYSTEM_LOCALE_STRING(SECTION_MAIN.path + ".System Locale"),

    //Chat Settings
    CHAT_DEBUG_OUTPUT_BOOLEAN(SECTION_CHAT.path + ".Chat Debug"),
    CHAT_PREFIX_STRING(SECTION_CHAT.path + ".Chat Prefix"),
    CHAT_CONSOLE_PREFIX(SECTION_CHAT.path + ".Console Prefix"),
    CHAT_DEBUG_COLOR(SECTION_CHAT.path + ".Debug Colour"),
    CHAT_INFO_COLOR(SECTION_CHAT.path + ".Info Colour"),
    CHAT_WARNING_COLOR(SECTION_CHAT.path + ".Warn Colour"),
    CHAT_SEVERE_COLOR(SECTION_CHAT.path + ".Severe Colour"),
    CHAT_ECONOMY_DIGITS_INT(SECTION_CHAT.path + ".Money Scale"),
    CHAT_ECONOMY_PREFIX_STRING(SECTION_CHAT.path + ".Money Prefix"),
    CHAT_ECONOMY_SUFFIX_STRING(SECTION_CHAT.path + ".Money Suffix"),
    CHAT_ECONOMY_SINGULAR_STRING(SECTION_CHAT.path + ".Money Singular"),
    CHAT_ECONOMY_PLURAL_STRING(SECTION_CHAT.path + ".Money Plural"),

    // Error bypass
    IGNORE_ALIAS_ERRORS_BOOLEAN(SECTION_MESSAGES.path + ".Ignore Alias Errors"),
    IGNORE_ITEM_ERRORS_BOOLEAN(SECTION_MESSAGES.path + ".Ignore Item Errors"),
    IGNORE_COMMAND_REGISTRY_BOOLEAN(SECTION_MESSAGES.path + ".Ignore Command Registry"),
    IGNORE_SAVE_MESSAGE_BOOLEAN(SECTION_MESSAGES.path + ".Ignore Save Messages"),

    // World Market settings
    WORLDS_ENABLE_NOTIFICATION_BOOLEAN(SECTION_WORLDS.path + ".Enable Notifications"),
    WORLDS_MARKET_SECTION(SECTION_WORLDS.path + ".Market"),
    WORLDS_MARKET_ENABLED_WORLDS_STRINGLIST(WORLDS_MARKET_SECTION.path + ".Enabled Worlds"),
    WORLDS_MARKET_ENABLE_ALL_WORLDS_BOOLEAN(WORLDS_MARKET_SECTION.path + ".Enable All Worlds"),
    WORLDS_MARKET_APPLY_ITEMS_BOOLEAN(WORLDS_MARKET_SECTION.path + ".Apply To Items"),
    WORLDS_MARKET_APPLY_ENCHANTS_BOOLEAN(WORLDS_MARKET_SECTION.path + ".Apply To Enchants"),
    WORLDS_MARKET_APPLY_EXP_BOOLEAN(WORLDS_MARKET_SECTION.path + ".Apply To EXP"),
    WORLDS_MARKET_ENABLE_MESSAGE_BOOLEAN(WORLDS_MARKET_SECTION.path + ".Enable Message"),

    WORLDS_ECONOMY_SECTION(SECTION_WORLDS.path + ".Economy"),
    WORLDS_ECONOMY_ENABLED_WORLDS_STRINGLIST(WORLDS_ECONOMY_SECTION.path + ".Enabled Worlds"),
    WORLDS_ECONOMY_ENABLE_ALL_WORLDS_BOOLEAN(WORLDS_ECONOMY_SECTION.path + ".Enable All Worlds"),
    WORLDS_ECONOMY_ENABLE_MESSAGE_BOOLEAN(WORLDS_ECONOMY_SECTION.path + ".Enable Message"),

    // World Economy Settings
    ECONOMY_MIN_SEND_AMOUNT_DOUBLE(SECTION_ECONOMY.path + ".Min Send Amount"),
    ECONOMY_BALTOP_REFRESH_INTEGER(SECTION_ECONOMY.path + ".ListBalances Refresh Timer"),

    //Market Settings
    MARKET_SAVE_TIMER_INTEGER(SECTION_MARKET.path + ".Save Timer"),
    MARKET_MAX_ITEM_VALUE_DOUBLE(SECTION_MARKET.path + ".Max Item Value"),
    MARKET_MIN_ITEM_VALUE_DOUBLE(SECTION_MARKET.path + ".Min Item Value"),

    MARKET_MATERIALS_ENABLE_BOOLEAN(SECTION_MARKET_MATERIALS.path + ".Enable"),
    MARKET_MATERIALS_BASE_QUANTITY_INTEGER(SECTION_MARKET_MATERIALS.path + ".Base Quantity"),
    MARKET_MATERIALS_BUY_TAX_FLOAT(SECTION_MARKET_MATERIALS.path + ".Buy Scale"),
    MARKET_MATERIALS_SELL_TAX_FLOAT(SECTION_MARKET_MATERIALS.path + ".Sell Scale"),
    MARKET_MATERIALS_DYN_PRICING_BOOLEAN(SECTION_MARKET_MATERIALS.path + ".Dynamic Pricing"),
    MARKET_MATERIALS_WHOLE_MARKET_INF_BOOLEAN(SECTION_MARKET_MATERIALS.path + ".Whole Market Inflation"),
    MARKET_MATERIALS_ITEM_DMG_SCALING_BOOLEAN(SECTION_MARKET_MATERIALS.path + ".Item Damage Scaling"),
    MARKET_MATERIALS_IGNORE_NAMED_ITEMS_BOOLEAN(SECTION_MARKET_MATERIALS.path + ".Ignore Named Items"),

    MARKET_ENCHANTS_ENABLE_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Enable"),
    MARKET_ENCHANTS_BASE_QUANTITY_INTEGER(SECTION_MARKET_ENCHANTS.path + ".Base Quantity"),
    MARKET_ENCHANTS_BUY_TAX_FLOAT(SECTION_MARKET_ENCHANTS.path + ".Buy Scale"),
    MARKET_ENCHANTS_SELL_TAX_FLOAT(SECTION_MARKET_ENCHANTS.path + ".Sell Scale"),
    MARKET_ENCHANTS_DYN_PRICING_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Dynamic Pricing"),
    MARKET_ENCHANTS_WHOLE_MARKET_INF_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Whole Market Inflation"),
    MARKET_ENCHANTS_ALLOW_UNSAFE_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Allow Unsafe Enchants"),

    MARKET_EXP_ENABLE_BOOLEAN(SECTION_MARKET_EXP.path + ".Enable"),
    MARKET_EXP_BASE_QUANTITY_INTEGER(SECTION_MARKET_EXP.path + ".Base Quantity"),
    MARKET_EXP_BUY_TAX_FLOAT(SECTION_MARKET_EXP.path + ".Buy Scale"),
    MARKET_EXP_SELL_TAX_FLOAT(SECTION_MARKET_EXP.path + ".Sell Scale"),
    MARKET_EXP_DYN_PRICING_BOOLEAN(SECTION_MARKET_EXP.path + ".Dynamic Pricing"),
    MARKET_EXP_WHOLE_MARKET_INF_BOOLEAN(SECTION_MARKET_EXP.path + ".Whole Market Inflation"),

    // Mail
    MAIL_ENABLE_BOOLEAN(SECTION_MAIL.path + ".Enable"),
    MAIL_NOTIFY_BOOLEAN(SECTION_MAIL.path + ".Enable Notifications"),
    MAIL_NOTIFY_SILENT_BOOLEAN(SECTION_MAIL.path + ".Enable Silent Notifications"),

    //Commands Settings
    COMMAND_PING_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".Ping"),
    COMMAND_SET_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Bal"),
    COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Edit Bal"),
    COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Clear Bal"),
    COMMAND_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Balance"),
    COMMAND_BALANCE_OTHER_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Balance Other"),
    COMMAND_SEND_CASH_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Send Cash"),
    COMMAND_LIST_BALANCES_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".ListBalances"),
    COMMAND_BUY_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Buy"),
    COMMAND_SELL_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Sell"),
    COMMAND_SELLALL_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Sell All"),
    COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Hand Sell"),
    COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Hand Buy"),
    COMMAND_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Value"),
    COMMAND_HAND_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Hand Value"),
    COMMAND_INFO_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Info"),
    COMMAND_ITEMS_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".List Items"),
    COMMAND_HAND_INFO_ENABLE_BOOLEAN(SECTION_COMMANDS_MARKET.path + ".Hand Info"),
    COMMAND_READ_MAIL_ENABLE_BOOLEAN(SECTION_COMMANDS_MAIL.path + ".Read Mail"),
    COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN(SECTION_COMMANDS_MAIL.path + ".Clear Mail"),
    COMMAND_E_SELL_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Sell"),
    COMMAND_E_SELL_ALL_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Sell All"),
    COMMAND_E_BUY_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Buy"),
    COMMAND_E_HAND_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Hand Value"),
    COMMAND_E_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Value"),
    COMMAND_E_INFO_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Info"),
    COMMAND_EXP_SELL_ENABLE_BOOLEAN(SECTION_COMMANDS_EXPERIENCE.path + ".EXP Sell"),
    COMMAND_EXP_BUY_ENABLE_BOOLEAN(SECTION_COMMANDS_EXPERIENCE.path + ".EXP Buy"),
    COMMAND_EHELP_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".E Help"),
    COMMAND_SET_STOCK_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Stock"),
    COMMAND_SET_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Value"),
    COMMAND_E_SET_STOCK_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".E Set Stock"),
    COMMAND_E_SET_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".E Set Value"),
    COMMAND_SAVE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Save"),
    COMMAND_RELOAD_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Reload"),
    COMMAND_BAN_ITEM_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Ban Item"),
    COMMAND_BAN_ENCHANT_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Ban Enchant"),
    COMMAND_ECONOMY_NOTIFICATIONS_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".Economy Notifications"),

    ;

    public final String path;

    Setting(String path) {
        this.path = path;
    }

}
