package com.pulse.module;

import com.pulse.Pulse;
import net.minecraft.client.Minecraft;

/*
    Author: Plusbox
 */
public abstract class Module {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private final String description;
    private final Category category;
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
}
