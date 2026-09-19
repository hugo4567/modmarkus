package com.example.modmarkus.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import com.example.modmarkus.command.CommandSpawnImage;

public class CommandHandler {
    
    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSpawnImage());
    }
}
