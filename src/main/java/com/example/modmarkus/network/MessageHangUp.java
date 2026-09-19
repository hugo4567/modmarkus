package com.example.modmarkus.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHangUp implements IMessage {
    
    public MessageHangUp() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessageHangUp, IMessage> {
        @Override
        public IMessage onMessage(MessageHangUp message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            
            // Exécuter sur le thread principal du serveur
            world.addScheduledTask(() -> {
                System.out.println("[ModMarkus] Raccroché par " + player.getName() + " - Lancement d'un éclair!");
                EntityLightningBolt bolt = new EntityLightningBolt(world, player.posX, player.posY, player.posZ, false);
                world.addWeatherEffect(bolt);
                
                // On peut aussi infliger un peu de dégâts si l'éclair ne le fait pas directement
                // (Normalement EntityLightningBolt s'en occupe)
            });
            
            return null;
        }
    }
}
