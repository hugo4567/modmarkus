package com.example.modmarkus.handler;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "modmarkus")
public class SoundRegistry {
    
    public static final ResourceLocation REDSUN_NAME = new ResourceLocation("modmarkus", "redsun");
    public static final ResourceLocation BANJO_NAME = new ResourceLocation("modmarkus", "banjo");
    public static final ResourceLocation SLIP_NAME = new ResourceLocation("modmarkus", "slip");
    
    public static final SoundEvent REDSUN = new SoundEvent(REDSUN_NAME).setRegistryName(REDSUN_NAME);
    public static final SoundEvent BANJO = new SoundEvent(BANJO_NAME).setRegistryName(BANJO_NAME);
    public static final SoundEvent SLIP = new SoundEvent(SLIP_NAME).setRegistryName(SLIP_NAME);
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        System.out.println("[ModMarkus] Enregistrement des sons...");
        event.getRegistry().registerAll(REDSUN, BANJO, SLIP);
        System.out.println("[ModMarkus] ✓ Sons enregistrés!");
    }
}
