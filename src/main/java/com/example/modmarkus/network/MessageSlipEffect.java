package com.example.modmarkus.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSlipEffect implements IMessage {
    private int duration;

    public MessageSlipEffect() {}

    public MessageSlipEffect(int duration) {
        this.duration = duration;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.duration);
    }

    public static class Handler implements IMessageHandler<MessageSlipEffect, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSlipEffect message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                com.example.modmarkus.handler.SlipHandler.setClientSlipTicks(message.duration);
            });
            return null;
        }
    }
}
