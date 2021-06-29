package me.edgrrrr.de.config;

public enum Setting {

	/**
	 * Values starting with "SECTION" are configuration sections, and do not store
	 * a configurable value
	 * Values not starting with "SECTION" are individual configurable values and
	 * can be set/got, they end in the type they return
	 * @param path
	 */

	//Setting Sections
	SECTION_MAIN       ( "main"     ),
	SECTION_CHAT       ( "chat"     ),
	SECTION_ECONOMY    ( "economy"  ),
	SECTION_MESSAGES    ( "messageIgnore"),
	SECTION_COMMANDS   ( "commands" ),
	SECTION_MARKET     ( "market"   ),

	SECTION_COMMANDS_ADMIN    ( SECTION_COMMANDS.path +  ".admin"   ),
	SECTION_COMMANDS_MAIL     ( SECTION_COMMANDS.path +  ".mail"    ),
	SECTION_COMMANDS_ECONOMY  ( SECTION_COMMANDS.path +  ".economy" ),
	SECTION_COMMANDS_MISC     ( SECTION_COMMANDS.path +  ".misc"    ),
	SECTION_COMMANDS_MARKET   ( SECTION_COMMANDS.path +  ".market"  ),
	SECTION_COMMANDS_ENCHANT  ( SECTION_COMMANDS.path +  ".enchant" ),

	SECTION_MARKET_MATERIALS ( SECTION_MARKET.path + ".materials" ),
	SECTION_MARKET_ENCHANTS  ( SECTION_MARKET.path + ".enchants"  ),

	//Main Settings
	MAIN_VERSION_STRING ( SECTION_MAIN.path + ".version" ),

	//Chat Settings
	CHAT_DEBUG_OUTPUT_BOOLEAN         ( SECTION_CHAT.path + ".chatDebug"       ),
	CHAT_PREFIX_STRING                ( SECTION_CHAT.path + ".chatPrefix"      ),
	CHAT_CONSOLE_PREFIX               ( SECTION_CHAT.path + ".consolePrefix"   ),
	CHAT_DEBUG_COLOR                  ( SECTION_CHAT.path + ".debugColour"     ),
	CHAT_INFO_COLOR                   ( SECTION_CHAT.path + ".infoColour"      ),
	CHAT_WARNING_COLOR                ( SECTION_CHAT.path + ".warnColour"   ),
	CHAT_SEVERE_COLOR                 ( SECTION_CHAT.path + ".severeColour"    ),
	CHAT_ECONOMY_DIGITS_INT			   ( SECTION_CHAT.path + ".moneyScale"    ),
	CHAT_ECONOMY_PREFIX_STRING			   ( SECTION_CHAT.path + ".moneyPrefix"    ),
	CHAT_ECONOMY_SINGULAR_STRING			   ( SECTION_CHAT.path + ".moneySingular"    ),
	CHAT_ECONOMY_PLURAL_STRING			   ( SECTION_CHAT.path + ".moneyPlural"    ),

	// Error bypass
	IGNORE_ALIAS_ERRORS_BOOLEAN			   ( SECTION_MESSAGES.path + ".ignoreAliasErrors"    ),
	IGNORE_MATERIAL_ERRORS_BOOLEAN			   ( SECTION_MESSAGES.path + ".ignoreMaterialErrors"    ),
	IGNORE_ENCHANT_ERRORS_BOOLEAN			   ( SECTION_MESSAGES.path + ".ignoreEnchantErrors"    ),
	IGNORE_COMMAND_REGISTRY_BOOLEAN			   ( SECTION_MESSAGES.path + ".ignoreCommandRegistry"    ),

	//Economy Settings
	ECONOMY_MIN_SEND_AMOUNT_DOUBLE    ( SECTION_ECONOMY.path + ".minSendAmount"     ),
	ECONOMY_MIN_BALANCE_DOUBLE        ( SECTION_ECONOMY.path + ".minAccountBalance" ),
	ECONOMY_PROVIDER_STRING        ( SECTION_ECONOMY.path + ".preferredProvider" ),

	//Market Settings
	MARKET_SAVE_TIMER_INTEGER   		     ( SECTION_MARKET.path + ".saveTimer"              ),

	MARKET_MATERIALS_ENABLE_BOOLEAN          ( SECTION_MARKET_MATERIALS.path + ".enable"       ),
	MARKET_MATERIALS_BASE_QUANTITY_INTEGER   ( SECTION_MARKET_MATERIALS.path + ".baseQuantity" ),
	MARKET_MATERIALS_BUY_TAX_FLOAT           ( SECTION_MARKET_MATERIALS.path + ".buyTax"       ),
	MARKET_MATERIALS_SELL_TAX_FLOAT          ( SECTION_MARKET_MATERIALS.path + ".sellTax"      ),
	MARKET_MATERIALS_DYN_PRICING_BOOLEAN      ( SECTION_MARKET_MATERIALS.path  + ".dynamicPricing"      ),
	MARKET_MATERIALS_WHOLE_MARKET_INF_BOOLEAN ( SECTION_MARKET_MATERIALS.path  + ".wholeMarketInflation"      ),
	MARKET_MATERIALS_ITEM_DMG_SCALING_BOOLEAN ( SECTION_MARKET_MATERIALS.path  + ".itemDamageScaling"      ),

	MARKET_ENCHANTS_ENABLE_BOOLEAN           ( SECTION_MARKET_ENCHANTS.path  + ".enable"       ),
	MARKET_ENCHANTS_BASE_QUANTITY_INTEGER    ( SECTION_MARKET_ENCHANTS.path  + ".baseQuantity" ),
	MARKET_ENCHANTS_BUY_TAX_FLOAT            ( SECTION_MARKET_ENCHANTS.path  + ".buyTax"       ),
	MARKET_ENCHANTS_SELL_TAX_FLOAT           ( SECTION_MARKET_ENCHANTS.path  + ".sellTax"      ),
	MARKET_ENCHANTS_DYN_PRICING_BOOLEAN      ( SECTION_MARKET_ENCHANTS.path  + ".dynamicPricing"      ),
	MARKET_ENCHANTS_WHOLE_MARKET_INF_BOOLEAN ( SECTION_MARKET_ENCHANTS.path  + ".wholeMarketInflation"      ),

	//Commands Settings
	COMMAND_PING_ENABLE_BOOLEAN           ( SECTION_COMMANDS_MISC.path    + ".ping"      ),
	COMMAND_SET_BALANCE_ENABLE_BOOLEAN    ( SECTION_COMMANDS_ADMIN.path   + ".setBal"    ),
	COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN   ( SECTION_COMMANDS_ADMIN.path   + ".editBal"   ),
	COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN  ( SECTION_COMMANDS_ADMIN.path   + ".clearBal"  ),
	COMMAND_BALANCE_ENABLE_BOOLEAN        ( SECTION_COMMANDS_ECONOMY.path + ".balance"   ),
	COMMAND_SEND_CASH_ENABLE_BOOLEAN      ( SECTION_COMMANDS_ECONOMY.path + ".sendCash"  ),
	COMMAND_BUY_ITEM_ENABLE_BOOLEAN       ( SECTION_COMMANDS_MARKET.path  + ".buyItem"   ),
	COMMAND_SELL_ITEM_ENABLE_BOOLEAN      ( SECTION_COMMANDS_MARKET.path  + ".sellItem"  ),
	COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN ( SECTION_COMMANDS_MARKET.path  + ".handSell"  ),
	COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN  ( SECTION_COMMANDS_MARKET.path  + ".handBuy"   ),
	COMMAND_VALUE_ENABLE_BOOLEAN          ( SECTION_COMMANDS_MARKET.path  + ".value"     ),
	COMMAND_HAND_VALUE_ENABLE_BOOLEAN     ( SECTION_COMMANDS_MARKET.path  + ".handValue" ),
	COMMAND_INFO_ENABLE_BOOLEAN           ( SECTION_COMMANDS_MARKET.path  + ".info"      ),
	COMMAND_HAND_INFO_ENABLE_BOOLEAN      ( SECTION_COMMANDS_MARKET.path  + ".handInfo"  ),
	COMMAND_READ_MAIL_ENABLE_BOOLEAN      ( SECTION_COMMANDS_MAIL.path    + ".readMail"  ),
	COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN     ( SECTION_COMMANDS_MAIL.path    + ".clearMail" ),
	COMMAND_E_SELL_ENABLE_BOOLEAN         ( SECTION_COMMANDS_ENCHANT.path + ".eSell" 	  ),
	COMMAND_E_BUY_ENABLE_BOOLEAN		  ( SECTION_COMMANDS_ENCHANT.path + ".eBuy" 	  ),
	COMMAND_E_HAND_VALUE_ENABLE_BOOLEAN	      ( SECTION_COMMANDS_ENCHANT.path + ".eHandValue"	  ),
	COMMAND_E_VALUE_ENABLE_BOOLEAN	      ( SECTION_COMMANDS_ENCHANT.path + ".eValue"	  ),
	COMMAND_E_INFO_ENABLE_BOOLEAN		  ( SECTION_COMMANDS_ENCHANT.path + ".eInfo"	  ),
	COMMAND_SET_STOCK_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".setStock"),
	COMMAND_SET_VALUE_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".setValue"),
	COMMAND_E_SET_STOCK_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".eSetStock"),
	COMMAND_E_SET_VALUE_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".eSetValue"),
	COMMAND_RELOAD_ENCHANTS_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".reloadEnchants"),
	COMMAND_RELOAD_MATERIALS_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".reloadMaterials"),
	COMMAND_SAVE_ENCHANTS_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".saveEnchants"),
	COMMAND_SAVE_MATERIALS_ENABLE_BOOLEAN (SECTION_COMMANDS_ADMIN.path + ".saveMaterials"),
	COMMAND_EHELP_ENABLE_BOOLEAN(SECTION_COMMANDS_MISC.path + ".ehelp")
	;

	public final String path;

	Setting(String path) {
		this.path = path;
	}

}
