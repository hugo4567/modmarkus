package com.example.modmarkus.network;

import com.example.modmarkus.item.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageMissedCall implements IMessage {
    
    public MessageMissedCall() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessageMissedCall, IMessage> {
        @Override
        public IMessage onMessage(MessageMissedCall message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                System.out.println("[ModMarkus] Traitement de l'appel manqué pour " + player.getName());
                // Parcourir l'inventaire pour trouver le téléphone
                boolean dropped = false;
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() == ModItems.TELEPHONE) {
                        System.out.println("[ModMarkus] Téléphone trouvé dans le slot " + i + ". Drop en cours...");
                        // Créer l'entité item pour le drop
                        EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY + 0.5, player.posZ, stack.copy());
                        entityItem.setNoPickupDelay();
                        entityItem.motionY = 0.2;
                        player.world.spawnEntity(entityItem);
                        
                        // Retirer de l'inventaire
                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        dropped = true;
                    }
                }
                
                if (dropped) {
                    player.inventory.markDirty();
                    if (player.openContainer != null) {
                        player.openContainer.detectAndSendChanges();
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    player.sendMessage(new TextComponentTranslation("chat.modmarkus.lost_something"));
                } else {
                    System.out.println("[ModMarkus] Aucun téléphone trouvé dans l'inventaire de " + player.getName());
                }

                // Faire spawner le punisher devant le joueur
                double yaw = Math.toRadians(player.rotationYaw);
                double spawnX = player.posX - Math.sin(yaw) * 2.0;
                double spawnZ = player.posZ + Math.cos(yaw) * 2.0;
                double spawnY = player.posY;

                com.example.modmarkus.entity.EntityPunisher punisher = new com.example.modmarkus.entity.EntityPunisher(player.world);
                punisher.setLocationAndAngles(spawnX, spawnY, spawnZ, player.rotationYaw + 180f, 0f);
                player.world.spawnEntity(punisher);
            });
            return null;
        }
    }
}
