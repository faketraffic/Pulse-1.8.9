package com.pulse.gui;

import com.pulse.Pulse;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.font.CustomFont;
import com.pulse.util.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    private final List<CategoryPanel> panels = new ArrayList<>();
    private String tooltip = null;
    private int tooltipX, tooltipY;

    public ClickGui() {
        int x = 30;
        for (Category category : Category.values()) {
            panels.add(new CategoryPanel(category, x, 30));
            x += CategoryPanel.WIDTH + 4;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tooltip = null;

        RenderUtils.drawRect(0, 0, width, height, 0x90000000);

        for (int i = 0; i < height; i += 40) {
            RenderUtils.drawRect(0, i, width, 0.5f, 0x08FFFFFF);
        }

        for (CategoryPanel panel : panels) {
            panel.draw(mouseX, mouseY, this);
        }

        if (tooltip != null && !tooltip.isEmpty()) {
            drawTooltip(tooltip, tooltipX, tooltipY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTooltip(String text, int mx, int my) {
        FontRenderer fr = mc.fontRendererObj;
        int tw = fr.getStringWidth(text) + 6;
        int th = 12;
        int tx = mx + 8;
        int ty = my - 4;
        RenderUtils.drawRect(tx, ty, tw, th, 0xF0101018);
        RenderUtils.drawRect(tx, ty, tw, 0.5f, 0xFF6C5CE7);
        fr.drawStringWithShadow(text, tx + 3, ty + 2, 0xFFCCCCCC);
    }

    public void setTooltip(String text, int x, int y) {
        this.tooltip = text;
        this.tooltipX = x;
        this.tooltipY = y;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CategoryPanel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, mouseButton)) return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (CategoryPanel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
            return;
        }
        for (CategoryPanel panel : panels) {
            if (panel.keyTyped(typedChar, keyCode)) return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            int mx = Mouse.getEventX() * width / mc.displayWidth;
            int my = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            for (CategoryPanel panel : panels) {
                if (panel.isHovered(mx, my)) {
                    panel.scroll(scroll > 0 ? -14 : 14);
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Module mod = Pulse.getInstance().getModuleManager().getModuleByName("ClickGUI");
        if (mod != null && mod.isEnabled()) {
            mod.setEnabled(false);
        }
    }
}
