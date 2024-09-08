package me.edgrrrr.de.help;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.lang.LangEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.edgrrrr.de.utils.ArrayUtils.paginator;

public class HelpManager extends DivinityModule {
    final Map<String, Help> helpMap;
    final int maxHelpReturns = 50;
    final int maxHelpPerPage = 10;

    public HelpManager(DEPlugin main) {
        super(main);
        this.helpMap = new ConcurrentHashMap<>();
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        this.loadHelp();
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }

    public Help get(String command) {
        // Clean
        command = command.toLowerCase().strip();

        // Quick Check
        Help help = this.helpMap.get(command.toLowerCase());
        if (help != null) {
            return help;
        }

        // Deep Check Aliases
        for (Help helpObject : this.helpMap.values()) {
            for (String alias : helpObject.getAliases()) {
                if (alias.equalsIgnoreCase(command)) {
                    return helpObject;
                }
            }
        }

        // No match
        return null;
    }

    /**
     * Uses the paginator function to paginate the help array
     *
     * @param helpArray  - array of help objects
     * @param maxPerPage - maximum number of help objects per page
     * @return - map of <Integer, List<Help>>
     */
    public static Map<Integer, List<Help>> paginate(Help[] helpArray, int maxPerPage) {
        Map<Integer, List<Object>> pages = paginator(helpArray, maxPerPage);
        Map<Integer, List<Help>> helpPages = new ConcurrentHashMap<>();
        for (Integer page : pages.keySet()) {
            helpPages.put(page, (List<Help>) (List<?>) pages.get(page));
        }

        return helpPages;
    }

    /**
     * Returns a list of help objects that match the term
     *
     * @param term - The term to search for
     * @return - Array of help objects
     */
    public Help[] getAll(@Nullable String term) {
        // If the term is null, return all help
        if (term == null) {
            return this.helpMap.values().toArray(new Help[0]);
        }

        // If the term is empty, return all help
        else if (term.isEmpty()) {
            return this.helpMap.values().toArray(new Help[0]);
        }

        // Standardise term
        term = term.toLowerCase().strip(); // Standardise term
        Set<Help> helpSet = new HashSet<>(); // Create help array

        // Priority store
        ArrayList<Help> priority0ArrayList = new ArrayList<>();
        ArrayList<Help> priority1ArrayList = new ArrayList<>();
        ArrayList<Help> priority2ArrayList = new ArrayList<>();
        ArrayList<Help> priority3ArrayList = new ArrayList<>();
        ArrayList<Help> priority4ArrayList = new ArrayList<>();
        ArrayList<Help> priority5ArrayList = new ArrayList<>();

        // Counter
        int counter = 0;

        // Loop through help, add any item that
        // - contains <term>
        // - equals <term>
        // - startswith <term>
        // - endswith <term>
        // - description contains <term>
        // - aliases contains <term>
        for (Help help : this.helpMap.values()) {
            if (counter >= this.maxHelpReturns) {
                break;
            } // Size limitation check

            String helpTitle = help.getCommand().toLowerCase().strip();
            String[] aliases = help.getAliases();
            String helpDescription = help.getDescription().toLowerCase().strip();

            // Matches - priority 0
            if (helpTitle.equalsIgnoreCase(term)) {
                priority0ArrayList.add(help);
                counter += 1;
                continue;
            }

            // Begins with - priority 1
            if (helpTitle.startsWith(term)) {
                priority1ArrayList.add(help);
                counter += 1;
                continue;
            }

            // Contains - priority 2
            if (helpTitle.contains(term)) {
                priority2ArrayList.add(help);
                counter += 1;
                continue;
            }

            // Endswith - priority 3
            if (helpTitle.endsWith(term)) {
                priority3ArrayList.add(help);
                counter += 1;
                continue;
            }

            // Description contains - priority 4
            if (helpDescription.contains(term)) {
                priority4ArrayList.add(help);
                counter += 1;
            }

            // Aliases - priority 5
            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(term) || alias.startsWith(term) || alias.contains(term) || alias.endsWith(term)) {
                    priority5ArrayList.add(help);
                    counter += 1;
                    break;
                }
            }
        }

        // Add by priority
        helpSet.addAll(priority0ArrayList);
        helpSet.addAll(priority1ArrayList);
        helpSet.addAll(priority2ArrayList);
        helpSet.addAll(priority3ArrayList);
        helpSet.addAll(priority4ArrayList);
        helpSet.addAll(priority5ArrayList);

        // Return array
        return helpSet.toArray(new Help[0]);
    }

    /**
     * Returns all the names of the help
     *
     * @param term - search term
     * @return - array of strings
     */
    public String[] getAllNames(String term) {
        return Arrays.stream(this.getAll(term)).map(Help::getCommand).toArray(String[]::new);
    }

    /**
     * Gets the page of the help
     *
     * @param term - search term
     * @param page - page number
     * @return - list of help objects
     */
    public List<Help> getPage(String term, int page) {
        // Get pages
        Map<Integer, List<Help>> pages = this.getPages(term);

        // If page exists, return it
        if (pages.containsKey(page)) {
            return pages.get(page);
        }

        // If requested page is greater than the number of pages, return the last page
        if (pages.size() <= page) {
            return pages.get(pages.size());
        }

        // Else, return the first page
        return pages.get(1);
    }

    /**
     * Gets the pages of the help
     *
     * @param term - search term
     * @return - map of <Integer, List<Help>>
     */
    public Map<Integer, List<Help>> getPages(String term) {
        return paginate(this.getAll(term), this.maxHelpPerPage);
    }

    /**
     * Loads the help from the plugin.yml
     */
    public void loadHelp() {
        Map<String, Map<String, Object>> commands = getMain().getDescription().getCommands();
        for (String command : commands.keySet()) {
            try {
                Map<String, Object> commandSection = commands.get(command);
                if (commandSection == null) {
                    this.getConsole().severe(LangEntry.HELP_NullEntry.get(getMain()), command);
                } else {
                    String commandName = command.toLowerCase();
                    this.helpMap.put(commandName, Help.fromConfig(commandName, commandSection));
                }
            } catch (Exception e) {
                this.getConsole().severe(LangEntry.HELP_EntryError.get(getMain()), command, e.getMessage());
            }
        }
        this.getConsole().info(LangEntry.HELP_HelpLoaded.get(getMain()), this.helpMap.size());
    }
}
