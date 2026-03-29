package com.pulse.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class BlurUtils {

    private static Framebuffer blurFbo1, blurFbo2;
    private static int kawaseProgram = -1;
    private static boolean initAttempted = false;

    private static void init() {
        if (initAttempted) return;
        initAttempted = true;

        if (!OpenGlHelper.shadersSupported) {
            System.err.println("[Pulse] Shaders not supported on this GPU");
            return;
        }

        kawaseProgram = ShaderUtil.createProgram(ShaderUtil.PASSTHROUGH_VERTEX, ShaderUtil.KAWASE_FRAGMENT);
        if (kawaseProgram == -1) {
            System.err.println("[Pulse] Failed to compile blur shader");
            initAttempted = false;
        } else {
            System.out.println("[Pulse] Blur shader compiled (program=" + kawaseProgram + ")");
        }
    }

    private static void setupFramebuffers(int width, int height) {
        if (blurFbo1 == null || blurFbo1.framebufferWidth != width || blurFbo1.framebufferHeight != height) {
            if (blurFbo1 != null) blurFbo1.deleteFramebuffer();
            if (blurFbo2 != null) blurFbo2.deleteFramebuffer();
            blurFbo1 = new Framebuffer(width, height, false);
            blurFbo2 = new Framebuffer(width, height, false);
            blurFbo1.setFramebufferFilter(GL11.GL_LINEAR);
            blurFbo2.setFramebufferFilter(GL11.GL_LINEAR);
        }
    }

    public static void blurArea(float x, float y, float w, float h, int iterations) {
        init();
        if (kawaseProgram == -1) return;

        Minecraft mc = Minecraft.getMinecraft();
        int displayW = mc.displayWidth;
        int displayH = mc.displayHeight;

        setupFramebuffers(displayW, displayH);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        blurFbo1.bindFramebuffer(true);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.bindTexture(mc.getFramebuffer().framebufferTexture);
        setupOrtho(displayW, displayH);
        drawQuad(displayW, displayH, mc.getFramebuffer());
        GL20.glUseProgram(kawaseProgram);

        int uTexelSize = GL20.glGetUniformLocation(kawaseProgram, "texelSize");
        int uOffset = GL20.glGetUniformLocation(kawaseProgram, "offset");
        int uTexture = GL20.glGetUniformLocation(kawaseProgram, "inTexture");

        GL20.glUniform1i(uTexture, 0);

        int passes = Math.min(iterations, 8);
        for (int i = 0; i < passes; i++) {
            Framebuffer src = (i % 2 == 0) ? blurFbo1 : blurFbo2;
            Framebuffer dst = (i % 2 == 0) ? blurFbo2 : blurFbo1;

            dst.bindFramebuffer(true);
            GlStateManager.bindTexture(src.framebufferTexture);
            setupOrtho(displayW, displayH);

            GL20.glUniform2f(uTexelSize, 1.0f / displayW, 1.0f / displayH);
            GL20.glUniform1f(uOffset, (float)(i + 1));

            drawQuad(displayW, displayH, src);
        }

        GL20.glUseProgram(0);
        mc.getFramebuffer().bindFramebuffer(true);

        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (x * scale),
                (int) ((sr.getScaledHeight() - y - h) * scale),
                (int) (w * scale),
                (int) (h * scale)
        );

        Framebuffer result = (passes % 2 == 0) ? blurFbo1 : blurFbo2;
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(result.framebufferTexture);
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);

        setupOrtho(displayW, displayH);
        drawQuad(displayW, displayH, result);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
        mc.entityRenderer.setupOverlayRendering();
    }

    private static void setupOrtho(int width, int height) {
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, width, height, 0, -1, 1);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }

    private static void drawQuad(int screenW, int screenH, Framebuffer sourceFbo) {
        float u = (float) sourceFbo.framebufferWidth / (float) sourceFbo.framebufferTextureWidth;
        float v = (float) sourceFbo.framebufferHeight / (float) sourceFbo.framebufferTextureHeight;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, v);  GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(u, v);  GL11.glVertex2f(screenW, 0);
        GL11.glTexCoord2f(u, 0);  GL11.glVertex2f(screenW, screenH);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0, screenH);
        GL11.glEnd();
    }
}
