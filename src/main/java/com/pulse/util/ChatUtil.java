package com.pulse.util;

import com.pulse.Pulse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil {

    private static final String PREFIX =
            EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + Pulse.NAME + EnumChatFormatting.DARK_GRAY + "] " +EnumChatFormatting.RESET;

    public static void send(String message) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText(PREFIX + message)
        );
    }
    public static void success(String message) {
        send(EnumChatFormatting.GREEN + message);
    }
    public static void warn(String message) {
        send(EnumChatFormatting.YELLOW + message);
    }
    public static void error(String message) {
        send(EnumChatFormatting.RED + message);
    }
}
