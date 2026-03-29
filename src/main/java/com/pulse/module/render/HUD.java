package com.pulse.module.render;

import com.pulse.Pulse;
import com.pulse.event.EventListener;
import com.pulse.event.events.EventRender2D;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.render.BlurUtils;
import com.pulse.util.render.RenderUtils;
import com.pulse.util.render.ShadowUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class HUD extends Module {

    public HUD() {
        super("HUD", "Glass watermark overlay", Category.RENDER, 0);
    }

    @EventListener
    public void onRender2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);

        float padding = 8;
        float x = 6;
        float y = 6;
        String title = Pulse.NAME;
        String version = "v" + Pulse.VERSION;
        float titleWidth = mc.fontRendererObj.getStringWidth(title);
        float versionWidth = mc.fontRendererObj.getStringWidth(version);
        float textWidth = titleWidth + 4 + versionWidth;
        float w = textWidth + padding * 2 + 4;
        float h = 22;
        float radius = 8;

        BlurUtils.blurArea(x, y, w, h, 6);


        ShadowUtils.drawDropShadow(x, y, w, h, radius);

    RenderUtils.drawRoundedRect(x, y, w, h, radius, 0x40FFFFFF);
        RenderUtils.drawRoundedOutline(x, y, w, h, radius, 1.0f, 0x30FFFFFF);


        GlStateManager.pushMatrix();
        float textY = y + (h - 8) / 2.0f;

        mc.fontRendererObj.drawStringWithShadow(
                title,
                x + padding,
                textY,
                0xFFFFFFFF
        );

        mc.fontRendererObj.drawStringWithShadow(
                version,
                x + padding + titleWidth + 4,
                textY,
                0xAAFFFFFF
        );

        GlStateManager.popMatrix();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }
}
