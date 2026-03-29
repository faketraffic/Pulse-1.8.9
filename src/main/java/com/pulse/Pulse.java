package com.pulse;

import com.pulse.command.CommandManager;
import com.pulse.event.EventBus;
import com.pulse.module.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pulse {

    public static final String NAME = "Pulse";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static Pulse instance;

    private EventBus eventBus;
    private ModuleManager moduleManager;
    private CommandManager commandManager;

    public void init() {
        //LOGGER.info("Initializing {} v{}", NAME, VERSION);

        eventBus = new EventBus();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();

        moduleManager.init();
        commandManager.init();
    }

    public void shutdown() {
        LOGGER.info("Shutting down {}...", NAME);
      //  break();  not needed ig
    }


    public static Pulse getInstance() {
        if (instance == null) {
            instance = new Pulse();
        }
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
