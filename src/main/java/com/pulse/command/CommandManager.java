package com.pulse.command;

import com.pulse.Pulse;
import com.pulse.util.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public static final String PREFIX = ".";

    private final List<Command> commands = new ArrayList<>();

    public void init() {
        //Pulse.LOGGER.info("Loaded {} commands.", commands.size());
    }

    public void register(Command command) {
        commands.add(command);
    }

    public boolean handle(String message) {
        if (!message.startsWith(PREFIX)) return false;

        String[] split = message.substring(PREFIX.length()).split(" ");
        String commandName = split[0].toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(commandName)) {
                try {
                    cmd.execute(args);
                } catch (Exception e) {
                    ChatUtil.error("Error: " + e.getMessage());
                }
                return true;
            }
            for (String alias : cmd.getAliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    try {
                        cmd.execute(args);
                    } catch (Exception e) {
                        ChatUtil.error("Error: " + e.getMessage());
                    }
                    return true;
                }
            }
        }

        ChatUtil.error("Unknown command: " + commandName);
        return true;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
