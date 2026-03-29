package com.pulse.gui;

import com.pulse.Pulse;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel {

    public static final int WIDTH = 108;
    private static final int HEADER_H = 22;

    private static final int COL_BG       = 0xFF0D0D12;
    private static final int COL_HEADER   = 0xFF131318;
    private static final int COL_ACCENT   = 0xFF6C5CE7;
    private static final int COL_BORDER   = 0xFF1E1E28;

    private final Category category;
    private final List<ModuleButton> buttons = new ArrayList<>();
    private float x, y;
    private float dragX, dragY;
    private boolean dragging;
    private boolean expanded = true;
    private float scrollOffset = 0;

    public CategoryPanel(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;

        for (Module m : Pulse.getInstance().getModuleManager().getModulesByCategory(category)) {
            buttons.add(new ModuleButton(m, this));
        }
    }

    public void draw(int mouseX, int mouseY, ClickGui gui) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        if (fr == null) return;

        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        float contentH = expanded ? getContentHeight() : 0;
        float totalH = HEADER_H + contentH;
        RenderUtils.drawRect(x + 2, y + 2, WIDTH, totalH, 0x40000000);
        RenderUtils.drawRect(x, y, WIDTH, totalH, COL_BG);
        RenderUtils.drawRect(x, y, WIDTH, HEADER_H, COL_HEADER);
        RenderUtils.drawRect(x, y, WIDTH, 2, COL_ACCENT);
        String name = category.getDisplayName().toUpperCase();
        float nw = fr.getStringWidth(name);
        fr.drawStringWithShadow(name, x + (WIDTH - nw) / 2f, y + (HEADER_H - 8) / 2f + 1, 0xFFE0E0E0);
        String ind = expanded ? "\u25BC" : "\u25B6";
        fr.drawStringWithShadow(ind, x + WIDTH - 12, y + (HEADER_H - 8) / 2f + 1, 0xFF555555);
        long enabledCount = buttons.stream().filter(b -> b.getModule().isEnabled()).count();
        if (enabledCount > 0) {
            String count = String.valueOf(enabledCount);
            int cw = fr.getStringWidth(count);
            RenderUtils.drawRect(x + 4, y + 6, cw + 4, 10, COL_ACCENT);
            fr.drawStringWithShadow(count, x + 6, y + 7, 0xFFFFFFFF);
        }
        if (expanded && !buttons.isEmpty()) {
            RenderUtils.drawRect(x + 4, y + HEADER_H, WIDTH - 8, 0.5f, COL_BORDER);

            RenderUtils.enableScissor(x, y + HEADER_H, WIDTH, contentH);

            float modY = y + HEADER_H + 1 + scrollOffset;
            for (ModuleButton btn : buttons) {
                btn.draw(x, modY, WIDTH, mouseX, mouseY, fr, gui);
                modY += btn.getHeight();
            }

            RenderUtils.disableScissor();
        }
        RenderUtils.drawRect(x - 0.5f, y, 0.5f, totalH, COL_BORDER);
        RenderUtils.drawRect(x + WIDTH, y, 0.5f, totalH, COL_BORDER);
        RenderUtils.drawRect(x, y + totalH, WIDTH, 0.5f, COL_BORDER);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int btn) {
        if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEADER_H) {
            if (btn == 0) {
                dragging = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
                return true;
            }
            if (btn == 1) {
                expanded = !expanded;
                return true;
            }
        }

        if (expanded) {
            float modY = y + HEADER_H + 1 + scrollOffset;
            for (ModuleButton button : buttons) {
                if (button.mouseClicked(x, modY, WIDTH, mouseX, mouseY, btn)) return true;
                modY += button.getHeight();
            }
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        for (ModuleButton b : buttons) b.mouseReleased(mouseX, mouseY, state);
    }

    public boolean keyTyped(char c, int keyCode) {
        for (ModuleButton b : buttons) {
            if (b.keyTyped(c, keyCode)) return true;
        }
        return false;
    }

    public boolean isHovered(int mx, int my) {
        float h = HEADER_H + (expanded ? getContentHeight() : 0);
        return mx >= x && mx <= x + WIDTH && my >= y && my <= y + h;
    }

    public void scroll(float amt) {
        float max = Math.max(0, getContentHeight() - 250);
        scrollOffset = Math.max(-max, Math.min(0, scrollOffset - amt));
    }

    private float getContentHeight() {
        float h = 1;
        for (ModuleButton b : buttons) h += b.getHeight();
        return h;
    }
}
