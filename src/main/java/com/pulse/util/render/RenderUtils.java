package com.pulse.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class RenderUtils {

    public static void drawRect(float x, float y, float w, float h, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(x, y + h, 0).color(r, g, b, a).endVertex();
        wr.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        wr.pos(x + w, y, 0).color(r, g, b, a).endVertex();
        wr.pos(x, y, 0).color(r, g, b, a).endVertex();
        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float x, float y, float w, float h, int topColor, int bottomColor) {
        float a1 = (topColor >> 24 & 0xFF) / 255.0F;
        float r1 = (topColor >> 16 & 0xFF) / 255.0F;
        float g1 = (topColor >> 8 & 0xFF) / 255.0F;
        float b1 = (topColor & 0xFF) / 255.0F;
        float a2 = (bottomColor >> 24 & 0xFF) / 255.0F;
        float r2 = (bottomColor >> 16 & 0xFF) / 255.0F;
        float g2 = (bottomColor >> 8 & 0xFF) / 255.0F;
        float b2 = (bottomColor & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(x, y + h, 0).color(r2, g2, b2, a2).endVertex();
        wr.pos(x + w, y + h, 0).color(r2, g2, b2, a2).endVertex();
        wr.pos(x + w, y, 0).color(r1, g1, b1, a1).endVertex();
        wr.pos(x, y, 0).color(r1, g1, b1, a1).endVertex();
        tess.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(float x, float y, float w, float h, float radius, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_POLYGON);
        arcVertices(x + w - radius, y + radius, radius, 270, 360);
        arcVertices(x + w - radius, y + h - radius, radius, 0, 90);
        arcVertices(x + radius, y + h - radius, radius, 90, 180);
        arcVertices(x + radius, y + radius, radius, 180, 270);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawRoundedOutline(float x, float y, float w, float h, float radius, float lineWidth, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(r, g, b, a);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        arcVertices(x + w - radius, y + radius, radius, 270, 360);
        arcVertices(x + w - radius, y + h - radius, radius, 0, 90);
        arcVertices(x + radius, y + h - radius, radius, 90, 180);
        arcVertices(x + radius, y + radius, radius, 180, 270);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawGradientRoundedRect(float x, float y, float w, float h, float radius, int topColor, int bottomColor) {
        float a1 = (topColor >> 24 & 0xFF) / 255.0F, r1 = (topColor >> 16 & 0xFF) / 255.0F;
        float g1 = (topColor >> 8 & 0xFF) / 255.0F, b1 = (topColor & 0xFF) / 255.0F;
        float a2 = (bottomColor >> 24 & 0xFF) / 255.0F, r2 = (bottomColor >> 16 & 0xFF) / 255.0F;
        float g2 = (bottomColor >> 8 & 0xFF) / 255.0F, b2 = (bottomColor & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor4f(r1, g1, b1, a1);
        arcVertices(x + w - radius, y + radius, radius, 270, 360);
        GL11.glColor4f(r2, g2, b2, a2);
        arcVertices(x + w - radius, y + h - radius, radius, 0, 90);
        arcVertices(x + radius, y + h - radius, radius, 90, 180);
        GL11.glColor4f(r1, g1, b1, a1);
        arcVertices(x + radius, y + radius, radius, 180, 270);
        GL11.glEnd();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    private static void arcVertices(double cx, double cy, double r, int startAngle, int endAngle) {
        for (int i = startAngle; i <= endAngle; i += 5) {
            double rad = Math.toRadians(i);
            GL11.glVertex2d(cx + Math.cos(rad) * r, cy + Math.sin(rad) * r);
        }
    }

    public static void enableScissor(float x, float y, float w, float h) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(x * scale), (int)((sr.getScaledHeight() - y - h) * scale), (int)(w * scale), (int)(h * scale));
    }

    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static int interpolateColor(int c1, int c2, float factor) {
        int a1 = (c1 >> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int a2 = (c2 >> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int a = (int)(a1 + (a2 - a1) * factor);
        int r = (int)(r1 + (r2 - r1) * factor);
        int g = (int)(g1 + (g2 - g1) * factor);
        int b = (int)(b1 + (b2 - b1) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
}
