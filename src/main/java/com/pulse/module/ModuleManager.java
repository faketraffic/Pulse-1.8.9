package com.pulse.module;

import com.pulse.Pulse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    Author: Plusbox
 */
public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void init() {
        //Pulse.LOGGER.info("Loaded {} test", modules.size());
    }


    public void register(Module module) {
        modules.add(module);
    }


    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) return (T) m;
        }
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }
    public void onKeyPress(int keyCode) {
        if (keyCode == 0) return;
        for (Module m : modules) {
            if (m.getKeyBind() == keyCode) {
                m.toggle();
            }
        }
    }
}
