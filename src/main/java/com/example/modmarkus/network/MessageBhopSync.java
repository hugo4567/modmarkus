package com.example.modmarkus.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBhopSync implements IMessage {
    private int streak;
    private float speed;

    public MessageBhopSync() {}

    public MessageBhopSync(int streak, float speed) {
        this.streak = streak;
        this.speed = speed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.streak = buf.readInt();
        this.speed = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.streak);
        buf.writeFloat(this.speed);
    }

    public static class Handler implements IMessageHandler<MessageBhopSync, IMessage> {
        @Override
        public IMessage onMessage(MessageBhopSync message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (message.streak > 0) {
                    player.fallDistance = 0.0F;
                    int amplifier = Math.min(message.streak / 2, 6);
                    player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, amplifier, true, false));
                } else {
                    player.removePotionEffect(MobEffects.SPEED);
                }
            });
            return null;
        }
    }
}
