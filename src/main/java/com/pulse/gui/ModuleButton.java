package com.pulse.gui;

import com.pulse.module.Module;
import com.pulse.setting.*;
import com.pulse.util.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public class ModuleButton {

    private static final int H = 14;
    private static final int SETTING_H = 13;

    private static final int COL_ACCENT    = 0xFF6C5CE7;
    private static final int COL_HOVER     = 0xFF16161E;
    private static final int COL_SUB_BG    = 0xFF0F0F15;
    private static final int COL_SUB_HOVER = 0xFF141420;
    private static final int COL_TEXT_ON   = 0xFFFFFFFF;
    private static final int COL_TEXT_OFF  = 0xFF777777;
    private static final int COL_SETTING   = 0xFFAAAAAA;

    private final Module module;
    private final CategoryPanel parent;
    private boolean settingsOpen;
    private boolean bindMode;
    private NumberSetting dragging;
    private long hoverStart;
    private boolean wasHovered;

    public ModuleButton(Module module, CategoryPanel parent) {
        this.module = module;
        this.parent = parent;
    }

    public Module getModule() { return module; }

    public void draw(float x, float y, float w, int mx, int my, FontRenderer fr, ClickGui gui) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + H;

        if (hovered && !wasHovered) hoverStart = System.currentTimeMillis();
        wasHovered = hovered;

        if (hovered) {
            RenderUtils.drawRect(x, y, w, H, COL_HOVER);
        }
        if (module.isEnabled()) {
            RenderUtils.drawRect(x, y, 2, H, COL_ACCENT);
        }

        int textCol = module.isEnabled() ? COL_TEXT_ON : (hovered ? 0xFFBBBBBB : COL_TEXT_OFF);
        fr.drawStringWithShadow(module.getName(), x + 7, y + (H - 8) / 2f, textCol);

        if (bindMode) {
            fr.drawStringWithShadow("[...]", x + w - fr.getStringWidth("[...]") - 3, y + (H - 8) / 2f, 0xFFFF5555);
        } else if (module.getKeyBind() != 0) {
            String kn = org.lwjgl.input.Keyboard.getKeyName(module.getKeyBind());
            if (kn != null) {
                String kt = "[" + kn + "]";
                fr.drawStringWithShadow(kt, x + w - fr.getStringWidth(kt) - 3, y + (H - 8) / 2f, 0xFF3A3A45);
            }
        }
        List<Setting<?>> settings = module.getSettings();
        if (!settings.isEmpty()) {
            int dotCol = settingsOpen ? COL_ACCENT : 0xFF444450;
            float dx = x + w - (module.getKeyBind() != 0 ? 28 : 10);
            float dy = y + H / 2f;
            RenderUtils.drawRect(dx, dy - 3, 2, 2, dotCol);
            RenderUtils.drawRect(dx, dy, 2, 2, dotCol);
            RenderUtils.drawRect(dx, dy + 3, 2, 2, dotCol);
        }

        if (hovered && System.currentTimeMillis() - hoverStart > 400 && gui != null) {
            String desc = module.getDescription();
            if (desc != null && !desc.isEmpty()) {
                gui.setTooltip(desc, mx, my);
            }
        }

    RenderUtils.drawRect(x + 5, y + H - 0.5f, w - 10, 0.5f, 0xFF1A1A24);

        if (settingsOpen && !settings.isEmpty()) {
            float sy = y + H;
            for (Setting<?> s : settings) {
                if (!s.isVisible()) continue;
                drawSetting(s, x, sy, w, mx, my, fr);
                sy += SETTING_H;
            }
        }
    }

    private void drawSetting(Setting<?> s, float x, float y, float w, int mx, int my, FontRenderer fr) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + SETTING_H;

        RenderUtils.drawRect(x, y, w, SETTING_H, hovered ? COL_SUB_HOVER : COL_SUB_BG);

        RenderUtils.drawRect(x + 3, y + 2, 1, SETTING_H - 4, 0xFF252535);

        if (s instanceof BooleanSetting) {
            BooleanSetting bs = (BooleanSetting) s;
            fr.drawStringWithShadow(s.getName(), x + 9, y + (SETTING_H - 8) / 2f, COL_SETTING);

            float tx = x + w - 18, ty = y + 3;
            float tw = 14, th = 7;
            int trackCol = bs.getValue() ? COL_ACCENT : 0xFF2A2A35;
            RenderUtils.drawRect(tx, ty, tw, th, trackCol);
            float knobX = bs.getValue() ? tx + tw - 5 : tx + 1;
            RenderUtils.drawRect(knobX, ty + 1, 4, 5, 0xFFDDDDDD);

        } else if (s instanceof NumberSetting) {
            NumberSetting ns = (NumberSetting) s;
            String label = s.getName();
            String val = ns.getIncrement() >= 1 ? String.valueOf(ns.getIntValue()) : String.format("%.1f", ns.getValue());
            fr.drawStringWithShadow(label, x + 9, y + 1, COL_SETTING);
            fr.drawStringWithShadow(val, x + w - fr.getStringWidth(val) - 4, y + 1, 0xFF666666);

            float bx = x + 9, by = y + SETTING_H - 3, bw = w - 18;
            float pct = (float) ((ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin()));
            RenderUtils.drawRect(bx, by, bw, 1.5f, 0xFF222230);
            RenderUtils.drawRect(bx, by, bw * pct, 1.5f, COL_ACCENT);
            RenderUtils.drawRect(bx + bw * pct - 1.5f, by - 1, 3, 3.5f, 0xFFDDDDDD);

            if (dragging == ns) {
                float p = Math.max(0, Math.min(1, (mx - bx) / bw));
                ns.setValue(ns.getMin() + (ns.getMax() - ns.getMin()) * p);
            }

        } else if (s instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting) s;
            fr.drawStringWithShadow(s.getName(), x + 9, y + (SETTING_H - 8) / 2f, COL_SETTING);
            String mode = ms.getValue();
            int mw = fr.getStringWidth(mode);
            RenderUtils.drawRect(x + w - mw - 8, y + 2, mw + 5, SETTING_H - 4, 0xFF1A1A28);
            fr.drawStringWithShadow(mode, x + w - mw - 5, y + (SETTING_H - 8) / 2f, COL_ACCENT);
        }
    }

    public boolean mouseClicked(float px, float py, float w, int mx, int my, int btn) {
        if (mx >= px && mx <= px + w && my >= py && my <= py + H) {
            if (btn == 0) { module.toggle(); return true; }
            if (btn == 1) {
                if (!module.getSettings().isEmpty()) settingsOpen = !settingsOpen;
                return true;
            }
            if (btn == 2) { bindMode = true; return true; }
        }

        if (settingsOpen) {
            float sy = py + H;
            for (Setting<?> s : module.getSettings()) {
                if (!s.isVisible()) continue;
                if (mx >= px && mx <= px + w && my >= sy && my <= sy + SETTING_H) {
                    if (s instanceof BooleanSetting && btn == 0) {
                        ((BooleanSetting) s).toggle();
                        return true;
                    }
                    if (s instanceof NumberSetting && btn == 0) {
                        dragging = (NumberSetting) s;
                        NumberSetting ns = (NumberSetting) s;
                        float bx = px + 9, bw = w - 18;
                        float p = Math.max(0, Math.min(1, (mx - bx) / bw));
                        ns.setValue(ns.getMin() + (ns.getMax() - ns.getMin()) * p);
                        return true;
                    }
                    if (s instanceof ModeSetting && btn == 0) {
                        ((ModeSetting) s).cycle();
                        return true;
                    }
                }
                sy += SETTING_H;
            }
        }
        return false;
    }

    public void mouseReleased(int mx, int my, int state) {
        dragging = null;
    }

    public boolean keyTyped(char c, int keyCode) {
        if (bindMode) {
            module.setKeyBind(keyCode == 1 ? 0 : keyCode);
            bindMode = false;
            return true;
        }
        return false;
    }

    public float getHeight() {
        float h = H;
        if (settingsOpen) {
            for (Setting<?> s : module.getSettings()) {
                if (s.isVisible()) h += SETTING_H;
            }
        }
        return h;
    }
}
