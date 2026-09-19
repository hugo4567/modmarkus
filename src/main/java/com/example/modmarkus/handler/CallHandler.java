package com.example.modmarkus.handler;

import com.example.modmarkus.gui.GuiIncomingCall;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = "modmarkus", value = Side.CLIENT)
public class CallHandler {

    private static final java.util.Random RANDOM = new java.util.Random();
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().world != null) {
            // On vérifie une fois par seconde (20 ticks) pour économiser des ressources
            if (Minecraft.getMinecraft().world.getTotalWorldTime() % 20 == 0) {
                // Probabilité d'environ 1/3600 par seconde (moyenne de 1 fois par heure)
                if (RANDOM.nextInt(3600) == 0 && Minecraft.getMinecraft().currentScreen == null) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiIncomingCall());
                }
            }
        }
    }
    
    /**
     * Obsolete
     */
    public static void reset() {
    }
}
