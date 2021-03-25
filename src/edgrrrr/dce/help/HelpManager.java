package edgrrrr.dce.help;

import edgrrrr.dce.DCEPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HelpManager {
    DCEPlugin app;
    HashMap<String, Help> helpMap;

    public HelpManager(DCEPlugin app) {
        this.app = app;
        this.helpMap = new HashMap<>();
    }

    public Help get(String command) {
        return this.helpMap.get(command.toLowerCase());
    }

    public Help[] getAll(String startsWith) {
        ArrayList<Help> helpArrayList = new ArrayList<>();
        startsWith = startsWith.toLowerCase();
        for (String commandName : helpMap.keySet()) {
            if (commandName.startsWith(startsWith)) {
                helpArrayList.add(helpMap.get(commandName));
            }
        }
        return helpArrayList.toArray(new Help[0]);
    }

    public String[] getAllNames(String startsWith) {
        ArrayList<String> nameArrayList = new ArrayList<>();
        Help[] helpArrayList = this.getAll(startsWith);
        for (Help help : helpArrayList) {
            nameArrayList.add(help.getCommand());
        }

        return nameArrayList.toArray(new String[0]);
    }

    public HashMap<Integer, Help[]> getPages(int pageSize) {
        HashMap<Integer, Help[]> pages = new HashMap<>();
        Help[] allHelp = this.helpMap.values().toArray(new Help[0]);

        int pageNum = 0;
        ArrayList<Help> page = new ArrayList<>();
        for (Help value : allHelp) {
            if (page.size() == pageSize) {
                pages.put(pageNum, page.toArray(new Help[0]));
                pageNum += 1;
                page = new ArrayList<>();
            }

            page.add(value);
        }

        if (!pages.containsKey(pageNum)) {
            pages.put(pageNum, page.toArray(new Help[0]));
        }

        return pages;
    }

    public void loadHelp() {
        Map<String, Map<String, Object>> commands = this.app.getDescription().getCommands();
        DCEPlugin.CONSOLE.debug(Arrays.toString(commands.keySet().toArray(new String[0])));
        for (String command : commands.keySet()) {
            try {
                Map<String, Object> commandSection = commands.get(command);
                if (commandSection == null) {
                    DCEPlugin.CONSOLE.severe(String.format("%s is null", command));
                } else {
                    String commandName = command.toLowerCase();
                    this.helpMap.put(commandName, Help.fromConfig(commandName, commandSection));
                }
            } catch (Exception e) {
                DCEPlugin.CONSOLE.severe(String.format("%s raised %s", command, e.getMessage()));
            }
        }
        DCEPlugin.CONSOLE.info(String.format("Loaded %d help objects", this.helpMap.size()));
    }
}
