package org.divinitycraft.divinityeconomy.help;

import java.util.List;
import java.util.Map;

public class Help {
    private final String command;
    private final String description;
    private final String[] aliases;
    private final String permissionNode;
    private final String[] usages;

    public Help(String command, String description, String[] aliases, String permissionNode, String[] usages) {
        this.command = command;
        this.description = description;
        this.aliases = aliases;
        this.permissionNode = permissionNode;
        this.usages = usages;
    }

    public static Help fromConfig(String command, Map<String, Object> commandSection) {
        String description = (String) commandSection.get("description");
        String permissionNode = (String) commandSection.get("permission");
        String[] aliases = ((List<?>) commandSection.get("aliases"))
                .stream()
                .map(Object::toString)
                .toArray(String[]::new);
        String[] usages = ((String) commandSection.get("usage")).split(", ");
        return new Help(command, description, aliases, permissionNode, usages);
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public String[] getUsages() {
        return usages;
    }

    public String getDescription(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = this.description.toCharArray();
        for (int idx = 0; idx < length && idx < chars.length; idx++) {
            stringBuilder.append(chars[idx]);
        }

        return stringBuilder.toString();
    }
}
