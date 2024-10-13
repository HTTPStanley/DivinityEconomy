package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.console.LogLevel;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.utils.ArrayUtils;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemList extends DivinityCommand {
    public static List<String> alphabeticalAliases = new ArrayList<>(Arrays.asList(
            "ALPHABETICAL",
            "ALPHA",
            "A"
    ));

    public static List<String> priceAliases = new ArrayList<>(Arrays.asList(
            "PRICE",
            "P",
            "VALUE",
            "V"
    ));

    public static List<String> stockAliases = new ArrayList<>(Arrays.asList(
            "STOCK",
            "S",
            "AMOUNT"
    ));

    /**
     * Constructor
     *
     * @param main
     */
    public ItemList(DEPlugin main) {
        super(main, "listitems", true, Setting.COMMAND_ITEMS_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        boolean ascending = false;
        boolean alpha = true;
        boolean value = false;
        boolean stock = false;
        int pageNumber = 0;
        String itemName = "";

        // Args
        // 0 Args:
        // - Returns alphabetical list of all items
        // 1 args:
        // - Specifies type/order of list
        // - Starts with +(ascending) -(descending)
        // - Then
        // - "alphabetical" - Alphabetical list
        // - "price" - Price list
        // - "stock" - Stock list (Inverse of price)
        switch (args.length) {
            case 3:
                String arg3 = args[2];
                pageNumber = Converter.getInt(arg3) - 1;

            case 2:
                String arg2 = args[1];
                if (arg2.startsWith("+")) {
                    ascending = false;
                    arg2 = arg2.replace("+", "");
                } else if (arg2.startsWith("-")) {
                    ascending = true;
                    arg2 = arg2.replace("-", "");
                }

                if (alphabeticalAliases.contains(arg2.toUpperCase())) {
                    alpha = true;
                } else if (priceAliases.contains(arg2.toUpperCase())) {
                    value = true;
                    alpha = false;
                } else if (stockAliases.contains(arg2.toUpperCase())) {
                    stock = true;
                    alpha = false;
                } else {
                    getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidArguments.get(getMain()), this.help.getUsages());
                    return true;
                }

            case 1:
                String arg1 = args[0];
                if (arg1.startsWith("*")) {
                    itemName = "";
                } else {
                    itemName = arg1;
                }
                break;

            case 0:
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Item names
        Set<String> itemNames = getMain().getMarkMan().searchItemNames(itemName);
        if (itemName.equals("")) itemName = "*";

        // If value
        if (value) {

            // Place all name-value pairs into map
            Map<String, Double> itemValues = new ConcurrentHashMap<>();
            for (String i : itemNames) {
                MarketableMaterial material = getMain().getMarkMan().getItem(i);

                itemValues.put(material.getName(), material.getManager().getBuyValue(material.getItemStack(1), 1).getValue());
            }

            // Sort map by entry value
            ArrayList<Map.Entry<String, Double>> sortedArray = new ArrayList<>(itemValues.entrySet());
            Collections.sort(sortedArray, Map.Entry.comparingByValue());
            if (!ascending) Collections.reverse(sortedArray);

            Map<Integer, List<Object>> itemPages = ArrayUtils.paginator(sortedArray.toArray(new Map.Entry[0]), 10);
            // Ensure page number fits within item pages
            if (pageNumber > itemPages.size() - 1 || pageNumber < 0) {
                getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
                return true;
            }

            // Get page and print values
            List<Map.Entry<String, Double>> page = new ArrayList<>();
            itemPages.get(pageNumber).forEach(obj -> page.add((Map.Entry<String, Double>) obj));

            getMain().getConsole().info(sender, LangEntry.ITEMLIST_Header.get(getMain()), itemName, pageNumber + 1, itemPages.keySet().size());
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_AscendingOrder.get(getMain()), ascending);
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_OrderBy.get(getMain()), LangEntry.W_price.get(getMain()));
            page.forEach(entry -> getMain().getConsole().send(sender, LogLevel.INFO, " -%s: %s", entry.getKey(), getMain().getConsole().formatMoney(entry.getValue())));
        } else if (stock) {

            // Place all name-stock pairs into map
            Map<String, Integer> itemValues = new ConcurrentHashMap<>();
            for (String i : itemNames) {
                MarketableMaterial material = getMain().getMarkMan().getItem(i);

                itemValues.put(material.getName(), material.getQuantity());
            }

            // Sort map by entry value
            ArrayList<Map.Entry<String, Integer>> sortedArray = new ArrayList<>(itemValues.entrySet());
            Collections.sort(sortedArray, Map.Entry.comparingByValue());
            if (!ascending) Collections.reverse(sortedArray);

            Map<Integer, List<Object>> itemPages = ArrayUtils.paginator(sortedArray.toArray(new Map.Entry[0]), 10);
            // Ensure page number fits within item pages
            if (pageNumber > itemPages.size() - 1 || pageNumber < 0) {
                getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
                return true;
            }

            List<Map.Entry<String, Integer>> page = new ArrayList<>();
            itemPages.get(pageNumber).forEach(obj -> page.add((Map.Entry<String, Integer>) obj));
            getMain().getConsole().info(sender, LangEntry.ITEMLIST_Header.get(getMain()), itemName, pageNumber + 1, itemPages.keySet().size());
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_AscendingOrder.get(getMain()), ascending);
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_OrderBy.get(getMain()), LangEntry.W_stock.get(getMain()));
            page.forEach(entry -> getMain().getConsole().send(sender, LogLevel.INFO, " -%s: %s", entry.getKey(), entry.getValue()));
        } else if (alpha) {

            // Place all name-name pairs into map
            Map<String, String> itemValues = new ConcurrentHashMap<>();
            for (String i : itemNames) {
                MarketableMaterial material = getMain().getMarkMan().getItem(i);

                itemValues.put(material.getName(), i);
            }

            // Sort map by entry value
            ArrayList<Map.Entry<String, String>> sortedArray = new ArrayList<>(itemValues.entrySet());
            Collections.sort(sortedArray, Map.Entry.comparingByValue());
            if (ascending) Collections.reverse(sortedArray);

            Map<Integer, List<Object>> itemPages = ArrayUtils.paginator(sortedArray.toArray(new Map.Entry[0]), 10);
            // Ensure page number fits within item pages
            if (pageNumber > itemPages.size() - 1 || pageNumber < 0) {
                getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
                return true;
            }

            List<Map.Entry<String, String>> page = new ArrayList<>();
            itemPages.get(pageNumber).forEach(obj -> page.add((Map.Entry<String, String>) obj));
            getMain().getConsole().info(sender, LangEntry.ITEMLIST_Header.get(getMain()), itemName, pageNumber + 1, itemPages.keySet().size());
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_AscendingOrder.get(getMain()), ascending);
            getMain().getConsole().warn(sender, LangEntry.ITEMLIST_OrderBy.get(getMain()), LangEntry.W_alphabet.get(getMain()));
            page.forEach(entry -> getMain().getConsole().send(sender, LogLevel.INFO, " -%s", entry.getKey()));
        }

        return true;
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return onPlayerCommand(null, args);
    }
}
