package com.example.modmarkus.handler;

import com.example.modmarkus.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.text.TextComponentTranslation;

@Mod.EventBusSubscriber(modid = "modmarkus")
public class PlayerSpawnHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!player.world.isRemote) {
            // Réinitialiser le CallHandler côté client via un paquet ?
            // Comme on est en 1.12.2 simple, on peut faire une astuce
            // ou simplement laisser le CallHandler se gérer côté client.
            
            boolean hasTelephone = false;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == ModItems.TELEPHONE) {
                    hasTelephone = true;
                    break;
                }
            }

            if (!hasTelephone) {
                // addItemStackToInventory renvoie false si l'inventaire est plein
                boolean added = player.inventory.addItemStackToInventory(new ItemStack(ModItems.TELEPHONE));
                if (!added) {
                    // Si l'inventaire est plein, on le drop au sol à la place de ne rien faire ? 
                    // L'utilisateur dit "ne remplace aucun item", ce qui est le cas par défaut de addItemStackToInventory.
                    // Pour être sûr, on ne fait rien de plus, addItemStackToInventory ne remplacera jamais un item existant.
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.world.isRemote) {
            EntityPlayer player = event.player;
            
            // Vérification pour le message (toutes les 10 secondes / 200 ticks)
            if (player.ticksExisted % 200 == 0) {
                if (!hasTelephone(player)) {
                    player.sendMessage(new TextComponentTranslation("chat.modmarkus.forgot_phone"));
                }
            }

            // Vérification pour le blindness et poison (toutes les minutes / 1200 ticks)
            if (player.ticksExisted % 1200 == 0) {
                if (!hasTelephone(player)) {
                    // Blindness pendant 2s (40 ticks)
                    player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.BLINDNESS, 40, 0));
                    // Poison pendant 2s (40 ticks)
                    player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.POISON, 40, 0));
                }
            }
        }
    }

    private static boolean hasTelephone(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.TELEPHONE) {
                return true;
            }
        }
        return false;
    }
}
