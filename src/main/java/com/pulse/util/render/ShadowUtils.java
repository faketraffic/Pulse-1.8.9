package com.pulse.util.render;

public class ShadowUtils {

    public static void drawShadow(float x, float y, float w, float h, float radius, float shadowSize, int shadowColor) {
        int layers = 10;
        int baseAlpha = (shadowColor >> 24) & 0xFF;

        for (int i = layers; i > 0; i--) {
            float expand = (float) i / layers * shadowSize;
            float alpha = (float)(layers - i) / layers;
            int a = (int)(alpha * baseAlpha);
            int color = (a << 24) | (shadowColor & 0x00FFFFFF);
            RenderUtils.drawRoundedRect(
                    x - expand,
                    y - expand + (expand * 0.3f),
                    w + expand * 2,
                    h + expand * 2,
                    radius + expand * 0.5f,
                    color
            );
        }
    }

    public static void drawDropShadow(float x, float y, float w, float h, float radius) {
        drawShadow(x, y, w, h, radius, 12.0f, 0x60000000);
    }
}
