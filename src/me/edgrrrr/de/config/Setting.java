package me.edgrrrr.de.config;

public enum Setting {

    /**
     * Values starting with "SECTION" are configuration sections, and do not store
     * a configurable value
     * Values not starting with "SECTION" are individual configurable values and
     * can be set/got, they end in the type they return
     *
     * @param path
     */

    //Setting Sections
    SECTION_MAIN("Main"),
    SECTION_CHAT("Chat"),
    SECTION_TAB("Tab Completion"),
    SECTION_FUZZY(SECTION_TAB.path + ".Fuzzy Search"),
    SECTION_ECONOMY("Economy"),
    SECTION_MESSAGES("Message Ignore"),
    SECTION_COMMANDS("Commands"),
    SECTION_MARKET("Market"),

    SECTION_COMMANDS_ADMIN(SECTION_COMMANDS.path + ".Admin"),
    SECTION_COMMANDS_MAIL(SECTION_COMMANDS.path + ".Mail"),
    SECTION_COMMANDS_ECONOMY(SECTION_COMMANDS.path + ".Economy"),
    SECTION_COMMANDS_MISC(SECTION_COMMANDS.path + ".Misc"),
    SECTION_COMMANDS_MARKET(SECTION_COMMANDS.path + ".Market"),
    SECTION_COMMANDS_ENCHANT(SECTION_COMMANDS.path + ".Enchant"),

    SECTION_MARKET_MATERIALS(SECTION_MARKET.path + ".Materials"),
    SECTION_MARKET_ENCHANTS(SECTION_MARKET.path + ".Enchants"),

    //Main Settings
    MAIN_VERSION_STRING(SECTION_MAIN.path + ".Version"),
    MAIN_ENABLE_PAPI_BOOLEAN(SECTION_MAIN.path + ".Enable PAPI"),

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

    //Economy Settings
    ECONOMY_MIN_SEND_AMOUNT_DOUBLE(SECTION_ECONOMY.path + ".Min Send Amount"),
    ECONOMY_PROVIDER_STRING(SECTION_ECONOMY.path + ".Preferred Provider"),
    ECONOMY_BALTOP_REFRESH_INTEGER(SECTION_ECONOMY.path + ".Baltop Refresh Timer"),
    ECONOMY_SSM_INTEGER(SECTION_ECONOMY.path + ".Smart Storage Max Size"),
    ECONOMY_MAX_LOGS_INTEGER(SECTION_ECONOMY.path + ".Max Logs"),

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

    MARKET_ENCHANTS_ENABLE_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Enable"),
    MARKET_ENCHANTS_BASE_QUANTITY_INTEGER(SECTION_MARKET_ENCHANTS.path + ".Base Quantity"),
    MARKET_ENCHANTS_BUY_TAX_FLOAT(SECTION_MARKET_ENCHANTS.path + ".Buy Scale"),
    MARKET_ENCHANTS_SELL_TAX_FLOAT(SECTION_MARKET_ENCHANTS.path + ".Sell Scale"),
    MARKET_ENCHANTS_DYN_PRICING_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Dynamic Pricing"),
    MARKET_ENCHANTS_WHOLE_MARKET_INF_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Whole Market Inflation"),
    MARKET_ENCHANTS_ALLOW_UNSAFE_BOOLEAN(SECTION_MARKET_ENCHANTS.path + ".Allow Unsafe Enchants"),

    //Commands Settings
    COMMAND_PING_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".Ping"),
    COMMAND_SET_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Bal"),
    COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Edit Bal"),
    COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Clear Bal"),
    COMMAND_BALANCE_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Balance"),
    COMMAND_SEND_CASH_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Send Cash"),
    COMMAND_BALTOP_ENABLE_BOOLEAN(SECTION_COMMANDS_ECONOMY.path + ".Baltop"),
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
    COMMAND_E_BUY_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Buy"),
    COMMAND_E_HAND_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Hand Value"),
    COMMAND_E_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Value"),
    COMMAND_E_INFO_ENABLE_BOOLEAN(SECTION_COMMANDS_ENCHANT.path + ".E Info"),
    COMMAND_SET_STOCK_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Stock"),
    COMMAND_SET_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Set Value"),
    COMMAND_E_SET_STOCK_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".E Set Stock"),
    COMMAND_E_SET_VALUE_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".E Set Value"),
    COMMAND_RELOAD_ENCHANTS_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Reload Enchants"),
    COMMAND_RELOAD_MATERIALS_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Reload Materials"),
    COMMAND_SAVE_ENCHANTS_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Save Enchants"),
    COMMAND_SAVE_MATERIALS_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Save Materials"),
    COMMAND_EHELP_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".E Help"),
    COMMAND_RELOAD_ENABLE_BOOLEAN(SECTION_COMMANDS_ADMIN.path + ".Reload");

    public final String path;

    Setting(String path) {
        this.path = path;
    }

}
