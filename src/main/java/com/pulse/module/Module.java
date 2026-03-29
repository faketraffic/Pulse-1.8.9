package com.pulse.module;

import com.pulse.Pulse;
import com.pulse.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Author: Plusbox
 */
public abstract class Module {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private int keyBind;
    private boolean enabled;

    public Module(String name, String description, Category category, int keyBind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyBind = keyBind;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
            Pulse.getInstance().getEventBus().register(this);
        } else {
            Pulse.getInstance().getEventBus().unregister(this);
            onDisable();
        }
    }

    protected void onEnable() {}

    protected void onDisable() {}


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void addSettings(Setting<?>... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }
}
