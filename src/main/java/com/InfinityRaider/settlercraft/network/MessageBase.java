package com.InfinityRaider.settlercraft.network;

import com.InfinityRaider.settlercraft.SettlerCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public abstract class MessageBase implements IMessage {
    public abstract Side getMessageHandlerSide();

    protected EntityPlayer readPlayerFromByteBuf(ByteBuf buf) {
        Entity entity = readEntityFromByteBuf(buf);
        return (entity instanceof EntityPlayer)?(EntityPlayer) entity:null;
    }

    protected void writePlayerToByteBuf(ByteBuf buf, EntityPlayer player) {
        writeEntityToByteBuf(buf, player);
    }

    protected Entity readEntityFromByteBuf(ByteBuf buf) {
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        int dimension = buf.readInt();
        return SettlerCraft.proxy.getEntityById(dimension, id);
    }

    protected void writeEntityToByteBuf(ByteBuf buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
            buf.writeInt(0);
        } else {
            buf.writeInt(e.getEntityId());
            buf.writeInt(e.worldObj.provider.getDimensionId());
        }
    }

    protected Item readItemFromByteBuf(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return  Item.itemRegistry.getObject(new ResourceLocation(itemName));
    }

    protected void writeItemToByteBuf(Item item, ByteBuf buf) {
        String itemName = item==null?"null":Item.itemRegistry.getNameForObject(item).toString();
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
    }

    protected ItemStack readItemStackFromByteBuf(ByteBuf buf) {
        return ByteBufUtils.readItemStack(buf);
    }

    protected void writeItemStackToByteBuf(ByteBuf buf, ItemStack stack) {
        ByteBufUtils.writeItemStack(buf, stack);
    }
}