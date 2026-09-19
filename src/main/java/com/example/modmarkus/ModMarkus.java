package com.example.modmarkus;

import com.example.modmarkus.entity.EntityImage;
import com.example.modmarkus.proxy.ClientProxyNew;
import com.example.modmarkus.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = "modmarkus", name = "Mod Markus", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class ModMarkus {

    public static final String MODID = "modmarkus";
    public static final String NAME = "Mod Markus";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.example.modmarkus.proxy.ClientProxyNew", serverSide = "com.example.modmarkus.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[ModMarkus] preInit - Enregistrement de l'entité...");
        
        // Initialiser le réseau
        com.example.modmarkus.network.NetworkHandler.init();
        
        // Enregistrer l'entité
        EntityRegistry.registerModEntity(
            new ResourceLocation("modmarkus", "image"),
            EntityImage.class,
            "EntityImage",
            0,
            this,
            64,
            10,
            true
        );
        EntityRegistry.registerModEntity(
            new ResourceLocation("modmarkus", "punisher"),
            com.example.modmarkus.entity.EntityPunisher.class,
            "JOHN",
            1,
            this,
            64,
            10,
            true
        );
        System.out.println("[ModMarkus] Entités enregistrées avec succès!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[ModMarkus] init - Enregistrement des renderers côté client...");
        proxy.registerRenders();
        
        // Enregistrer le handler de spawn
        MinecraftForge.EVENT_BUS.register(new com.example.modmarkus.handler.AutoSpawnHandler());
        MinecraftForge.EVENT_BUS.register(new com.example.modmarkus.handler.SlipHandler());
        System.out.println("[ModMarkus] Handlers enregistrés!");
    }
    
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        System.out.println("[ModMarkus] Enregistrement des commandes...");
        // Enregistrer les commandes
        event.registerServerCommand(new com.example.modmarkus.command.CommandSpawnImage());
        event.registerServerCommand(new com.example.modmarkus.command.CommandMsg2());
        event.registerServerCommand(new com.example.modmarkus.command.CommandAppel());
        event.registerServerCommand(new com.example.modmarkus.trade.CommandTrade());
        System.out.println("[ModMarkus] Commandes enregistrées!");
    }
}
