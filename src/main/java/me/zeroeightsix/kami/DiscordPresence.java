package me.zeroeightsix.kami;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.zeroeightsix.kami.module.modules.misc.DiscordSettings;
import me.zeroeightsix.kami.util.RichPresence;
import net.minecraft.client.Minecraft;

import static me.zeroeightsix.kami.KamiMod.APP_ID;
import static me.zeroeightsix.kami.KamiMod.MODULE_MANAGER;

/**
 * @author S-B99
 * Updated by S-B99 on 13/01/20
 * Updated (slightly) by Dewy on 3rd April 2020
 */
public class DiscordPresence {
    public static DiscordRichPresence presence;
    private static boolean connected;
    private static final DiscordRPC rpc;
    private static String details;
    private static String state;
    private static DiscordSettings discordSettings;

    public static void start() {
        KamiMod.log.info("Starting Discord RPC");
        if (DiscordPresence.connected) return;
        DiscordPresence.connected = true;

        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordPresence.rpc.Discord_Initialize(APP_ID, handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;

        /* update rpc normally */
        setRpcFromSettings();

        /* update rpc while thread isn't interrupted  */
        new Thread(DiscordPresence::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler").start();
        KamiMod.log.info("Discord RPC initialised successfully");
    }

    public static void end() {
        KamiMod.log.info("Shutting down Discord RPC...");

        DiscordPresence.connected = false;

        DiscordPresence.rpc.Discord_Shutdown();
    }

    private static void setRpcFromSettingsNonInt() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DiscordPresence.rpc.Discord_RunCallbacks();
                discordSettings = MODULE_MANAGER.getModuleT(DiscordSettings.class);
                String separator = " | ";
                details = discordSettings.getLine(discordSettings.line1Setting.getValue()) + separator + discordSettings.getLine(discordSettings.line3Setting.getValue());
                state = discordSettings.getLine(discordSettings.line2Setting.getValue()) + separator + discordSettings.getLine(discordSettings.line4Setting.getValue());
                DiscordPresence.presence.details = details;
                DiscordPresence.presence.state = state;
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
            }
            catch (Exception e2) { e2.printStackTrace(); }
            try { Thread.sleep(4000L); }
            catch (InterruptedException e3) { e3.printStackTrace(); }
        }
    }
    private static void setRpcFromSettings() {
        discordSettings = MODULE_MANAGER.getModuleT(DiscordSettings.class);
        details = discordSettings.getLine(discordSettings.line1Setting.getValue()) + " " + discordSettings.getLine(discordSettings.line3Setting.getValue());
        state = discordSettings.getLine(discordSettings.line2Setting.getValue()) + " " + discordSettings.getLine(discordSettings.line4Setting.getValue());
        DiscordPresence.presence.details = details;
        DiscordPresence.presence.state = state;
        DiscordPresence.presence.largeImageKey = "kami";
        DiscordPresence.presence.largeImageText = "blue.bella.wtf";
        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
        DiscordPresence.connected = false;
    }

    public static void setCustomIcons() {
        if (RichPresence.INSTANCE.customUsers != null) {
            for (RichPresence.CustomUser user : RichPresence.INSTANCE.customUsers) {
                if (user.uuid.equalsIgnoreCase(Minecraft.getMinecraft().session.getProfile().getId().toString())) {
                    switch (Integer.parseInt(user.type)) {
                        case 0: {
                            presence.smallImageKey = "booster";
                            presence.smallImageText = "booster uwu";
                            break;
                        }
                        case 1: {
                            presence.smallImageKey = "inviter";
                            presence.smallImageText = "inviter owo";
                            break;
                        }
                        case 2: {
                            presence.smallImageKey = "giveaway";
                            presence.smallImageText = "giveaway winner";
                            break;
                        }
                        case 3: {
                            presence.smallImageKey = "contest";
                            presence.smallImageText = "contest winner";
                            break;
                        }
                        case 4: {
                            presence.smallImageKey = "nine";
                            presence.smallImageText = "900th member";
                            break;
                        }
                        case 5: {
                            presence.smallImageKey = "github1";
                            presence.smallImageText = "contributor!! uwu";
                            break;
                        }
                        default: {
                            presence.smallImageKey = "donator2";
                            presence.smallImageText = "donator <3";
                            break;
                        }
                    }
                }
            }
        }
    }
}
