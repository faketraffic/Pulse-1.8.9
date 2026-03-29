package com.pulse.module.render;

import com.pulse.Pulse;
import com.pulse.event.EventListener;
import com.pulse.event.events.EventRender2D;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.setting.BooleanSetting;
import com.pulse.setting.ModeSetting;
import com.pulse.setting.NumberSetting;
import com.pulse.util.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArrayList extends Module {

    private final ModeSetting colorMode = new ModeSetting("Color", "Rainbow", "Rainbow", "Static", "Fade");
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final NumberSetting bgOpacity = new NumberSetting("BG Opacity", 80, 0, 255, 5);
    private final BooleanSetting border = new BooleanSetting("Border", true);
    private final ModeSetting borderSide = new ModeSetting("Border Side", "Right", "Right", "Left", "Top", "Outline");
    private final BooleanSetting rightSide = new BooleanSetting("Right Side", true);
    private final BooleanSetting customFont = new BooleanSetting("Custom Font", false);
    private final BooleanSetting lowercase = new BooleanSetting("Lowercase", false);

    private final List<ListEntry> entries = new CopyOnWriteArrayList<>();

    public ArrayList() {
        super("ArrayList", "Displays enabled modules", Category.RENDER, 0);
        addSettings(colorMode, background, bgOpacity, border, borderSide, rightSide, customFont, lowercase);
        bgOpacity.setVisibility(background::getValue);
        borderSide.setVisibility(border::getValue);
    }

    @EventListener
    public void onRender2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        if (fr == null) return;

        List<Module> modules = Pulse.getInstance().getModuleManager().getModules();
        java.util.List<Module> active = new java.util.ArrayList<>();
        for (Module m : modules) {
            if (m.isEnabled() && m != this && !(m instanceof HUD)) {
                active.add(m);
            }
        }
        active.sort(Comparator.comparingInt((Module m) -> getWidth(fr, m)).reversed());

        syncEntries(active);

        float yOffset = 2;
        int index = 0;

        for (ListEntry entry : entries) {
            entry.update();
            if (entry.animation <= 0.01f && !entry.active) continue;

            String text = getDisplayName(entry.module);
            float textWidth = getWidth(fr, entry.module);
            float height = fr.FONT_HEIGHT + 4;

            float x;
            float bgX;
            float bgW;

            if (rightSide.getValue()) {
                x = sr.getScaledWidth() - textWidth * entry.animation - 4;
                bgX = x - 2;
                bgW = textWidth + 6;
            } else {
                x = 4 - textWidth * (1 - entry.animation);
                bgX = 0;
                bgW = textWidth + 8;
            }
            float y = yOffset;

            int color = getColor(index);
            int alpha = (int) (255 * entry.animation);
            color = (alpha << 24) | (color & 0x00FFFFFF);

            if (background.getValue()) {
                int bgAlpha = (int) (bgOpacity.getValue() * entry.animation);
                if (rightSide.getValue()) {
                    RenderUtils.drawRect(bgX, y, bgW, height, (bgAlpha << 24));
                } else {
                    RenderUtils.drawRect(bgX, y, bgW * entry.animation, height, (bgAlpha << 24));
                }
            }

            // Border
            if (border.getValue()) {
                int borderColor = color | 0xFF000000;
                String side = borderSide.getValue();
                if (side.equals("Right") && rightSide.getValue()) {
                    RenderUtils.drawRect(sr.getScaledWidth() - 1.5f, y, 1.5f, height, borderColor);
                } else if (side.equals("Left") && !rightSide.getValue()) {
                    RenderUtils.drawRect(0, y, 1.5f, height, borderColor);
                } else if (side.equals("Left") && rightSide.getValue()) {
                    RenderUtils.drawRect(bgX, y, 1.5f, height, borderColor);
                } else if (side.equals("Right") && !rightSide.getValue()) {
                    RenderUtils.drawRect(bgW * entry.animation, y, 1.5f, height, borderColor);
                } else if (side.equals("Top")) {
                    RenderUtils.drawRect(bgX, y, bgW, 1, borderColor);
                } else if (side.equals("Outline")) {
                    float ox = rightSide.getValue() ? bgX : 0;
                    float ow = rightSide.getValue() ? bgW : bgW * entry.animation;
                    RenderUtils.drawRect(ox, y, ow, 0.5f, borderColor);
                    RenderUtils.drawRect(ox, y + height - 0.5f, ow, 0.5f, borderColor);
                    if (rightSide.getValue()) {
                        RenderUtils.drawRect(ox, y, 0.5f, height, borderColor);
                    } else {
                        RenderUtils.drawRect(ox + ow - 0.5f, y, 0.5f, height, borderColor);
                    }
                }
            }

            GlStateManager.pushMatrix();
            if (customFont.getValue() && Pulse.getInstance().getFontManager().getNormal() != null) {
                Pulse.getInstance().getFontManager().getNormal().drawStringWithShadow(text, x, y + 1, color);
            } else {
                fr.drawStringWithShadow(text, x, y + 2, color);
            }
            GlStateManager.popMatrix();

            yOffset += height * entry.animation;
            index++;
        }
    }

    private String getDisplayName(Module m) {
        return lowercase.getValue() ? m.getName().toLowerCase() : m.getName();
    }

    private int getWidth(FontRenderer fr, Module m) {
        String name = getDisplayName(m);
        if (customFont.getValue() && Pulse.getInstance().getFontManager().getNormal() != null) {
            return (int) Pulse.getInstance().getFontManager().getNormal().getStringWidth(name);
        }
        return fr.getStringWidth(name);
    }

    private void syncEntries(List<Module> active) {
        for (ListEntry entry : entries) {
            entry.active = active.contains(entry.module);
        }
        for (Module m : active) {
            boolean found = false;
            for (ListEntry entry : entries) {
                if (entry.module == m) { found = true; break; }
            }
            if (!found) entries.add(new ListEntry(m));
        }
        entries.removeIf(e -> !e.active && e.animation <= 0.01f);

        FontRenderer fr = mc.fontRendererObj;
        entries.sort(Comparator.comparingInt((ListEntry e) -> getWidth(fr, e.module)).reversed());
    }

    private int getColor(int index) {
        long time = System.currentTimeMillis();

        if (colorMode.is("Static")) {
            return 0xFFB040F0;
        }
        if (colorMode.is("Fade")) {
            float hue = (float) ((time / 3000.0 + index * 0.05) % 1.0);
            return java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);
        }

        float hue = (float) ((time / 2000.0 + index * 0.08) % 1.0);
        return java.awt.Color.HSBtoRGB(hue, 0.65f, 1.0f);
    }

    private static class ListEntry {
        final Module module;
        boolean active = true;
        float animation = 0f;

        ListEntry(Module module) {
            this.module = module;
        }

        void update() {
            float target = active ? 1f : 0f;
            animation += (target - animation) * 0.15f;
            if (Math.abs(animation - target) < 0.01f) animation = target;
        }
    }
}
