package com.example.modmarkus.handler;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.ServerChatEvent;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "modmarkus")
public class ChatEventHandler {
    
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        String message = event.getMessage();
        EntityPlayer player = event.getPlayer();
        MinecraftServer server = event.getPlayer().getServer();
        
        // Si le joueur tape "~spawn", spawn l'image
        if (message.equalsIgnoreCase("~spawn")) {
            event.setCanceled(true); // Masquer le message du chat
            
            // Exécuter sur le thread principal du serveur (pas le network thread!)
            server.addScheduledTask(() -> spawnImageForPlayer(player));
        }
    }
    
    private static void spawnImageForPlayer(EntityPlayer player) {
        Random random = new Random();
        
        // Position aléatoire autour du joueur
        double offsetX = (random.nextDouble() - 0.5) * 8.0;
        double offsetZ = (random.nextDouble() - 0.5) * 8.0;
        
        double spawnX = player.posX + offsetX;
        double spawnY = player.posY + 1.0;
        double spawnZ = player.posZ + offsetZ;
        
        EntityImage entity = new EntityImage(player.world, spawnX, spawnY, spawnZ);
        player.world.spawnEntity(entity);
        
        System.out.println("[ModMarkus] Image spawnée via ~spawn à (" + Math.round(spawnX) + ", " + Math.round(spawnY) + ", " + Math.round(spawnZ) + ")");
    }
}
