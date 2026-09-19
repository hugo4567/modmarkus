package com.example.modmarkus.trade;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTradeReady implements IMessage {

    private boolean ready;

    public MessageTradeReady() {}

    public MessageTradeReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.ready = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(ready);
    }

    public static class Handler implements IMessageHandler<MessageTradeReady, IMessage> {
        @Override
        public IMessage onMessage(MessageTradeReady message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                Container container = player.openContainer;
                if (container instanceof ContainerTrade) {
                    ((ContainerTrade) container).setReady(player, message.ready);
                }
            });
            return null;
        }
    }
}
