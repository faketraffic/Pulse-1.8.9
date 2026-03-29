package com.pulse.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        int index = modes.indexOf(getValue());
        setValue(modes.get((index + 1) % modes.size()));
    }

    public boolean is(String mode) {
        return getValue().equalsIgnoreCase(mode);
    }
}
