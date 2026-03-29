package com.pulse.util.font;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class CustomFont {

    private static final int ATLAS_SIZE = 512;
    private static final int PAD = 2;
    private static final float SCALE = 0.5f;

    private final Font awtFont;
    private int textureId = -1;
    private final Map<Character, Glyph> glyphs = new HashMap<>();
    private float height;

    public CustomFont(String name, int style, int px) {
        this.awtFont = new Font(name, style, px * 2);
    }

    public CustomFont(Font font, int px) {
        this.awtFont = font.deriveFont((float) (px * 2));
    }

    public void build() {
        BufferedImage img = new BufferedImage(ATLAS_SIZE, ATLAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setFont(awtFont);
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        FontRenderContext frc = g2.getFontRenderContext();
        float asc = fm.getAscent();

        int penX = PAD, penY = PAD, rowH = 0;

        for (int i = 32; i < 256; i++) {
            char ch = (char) i;
            if (!awtFont.canDisplay(ch)) continue;

            Rectangle2D r = awtFont.getStringBounds(String.valueOf(ch), frc);
            int gw = (int) Math.ceil(r.getWidth()) + 2;
            int gh = fm.getHeight() + 2;

            if (penX + gw + PAD > ATLAS_SIZE) {
                penX = PAD;
                penY += rowH + PAD;
                rowH = 0;
            }
            if (penY + gh + PAD > ATLAS_SIZE) break;

            g2.drawString(String.valueOf(ch), penX + 1, penY + asc);

            glyphs.put(ch, new Glyph(
                    (float) penX / ATLAS_SIZE,
                    (float) penY / ATLAS_SIZE,
                    (float) (penX + gw) / ATLAS_SIZE,
                    (float) (penY + gh) / ATLAS_SIZE,
                    gw, gh
            ));

            rowH = Math.max(rowH, gh);
            penX += gw + PAD;
        }
        g2.dispose();
        Glyph sample = glyphs.get('A');
        height = sample != null ? sample.height * SCALE : fm.getHeight() * SCALE;
        int[] px = new int[ATLAS_SIZE * ATLAS_SIZE];
        img.getRGB(0, 0, ATLAS_SIZE, ATLAS_SIZE, px, 0, ATLAS_SIZE);

        IntBuffer buf = BufferUtils.createIntBuffer(px.length);
        buf.put(px);
        buf.flip();

        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, ATLAS_SIZE, ATLAS_SIZE, 0,
                GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
    }

    public float drawString(String text, float x, float y, int color) {
        if (text == null || text.isEmpty() || textureId == -1) return x;

        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(textureId);
        GlStateManager.color(r, g, b, a);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(SCALE, SCALE, 1f);

        float drawX = 0;
        GL11.glBegin(GL11.GL_QUADS);
        for (int i = 0; i < text.length(); i++) {
            Glyph gl = glyphs.get(text.charAt(i));
            if (gl == null) {
                gl = glyphs.get('?');
                if (gl == null) { drawX += 8; continue; }
            }

            float x0 = drawX, y0 = 0;
            float x1 = drawX + gl.width, y1 = gl.height;

            GL11.glTexCoord2f(gl.u, gl.v);   GL11.glVertex2f(x0, y0);
            GL11.glTexCoord2f(gl.u, gl.v2);  GL11.glVertex2f(x0, y1);
            GL11.glTexCoord2f(gl.u2, gl.v2); GL11.glVertex2f(x1, y1);
            GL11.glTexCoord2f(gl.u2, gl.v);  GL11.glVertex2f(x1, y0);

            drawX += gl.width;
        }
        GL11.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);

        return x + drawX * SCALE;
    }

    public float drawStringWithShadow(String text, float x, float y, int color) {
        int alpha = (color >> 24) & 0xFF;
        int shadowAlpha = Math.max(20, alpha / 4);
        drawString(text, x + 0.5f, y + 0.5f, (shadowAlpha << 24));
        return drawString(text, x, y, color);
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2f, y, color);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        return drawStringWithShadow(text, x - getStringWidth(text) / 2f, y, color);
    }

    public float getStringWidth(String text) {
        if (text == null || text.isEmpty()) return 0;
        float w = 0;
        for (int i = 0; i < text.length(); i++) {
            Glyph gl = glyphs.get(text.charAt(i));
            if (gl != null) w += gl.width;
            else w += 8;
        }
        return w * SCALE;
    }

    public float getHeight() {
        return height;
    }

    public boolean isBuilt() {
        return textureId != -1;
    }

    private static class Glyph {
        final float u, v, u2, v2;
        final int width, height;
        Glyph(float u, float v, float u2, float v2, int w, int h) {
            this.u = u; this.v = v; this.u2 = u2; this.v2 = v2;
            this.width = w; this.height = h;
        }
    }
}
