package com.pulse.module.render;

import com.pulse.gui.ClickGui;
import com.pulse.module.Category;
import com.pulse.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {

    private ClickGui clickGui;

    public ClickGuiModule() {
        super("ClickGUI", "Opens the module configuration GUI", Category.RENDER, Keyboard.KEY_RSHIFT);
    }

    @Override
    protected void onEnable() {
        if (clickGui == null) {
            clickGui = new ClickGui();
        }
        mc.displayGuiScreen(clickGui);
    }

    @Override
    protected void onDisable() {
    }

    public ClickGui getClickGui() {
        if (clickGui == null) {
            clickGui = new ClickGui();
        }
        return clickGui;
    }
}
