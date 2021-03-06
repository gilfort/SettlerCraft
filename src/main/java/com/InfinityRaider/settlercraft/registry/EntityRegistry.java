package com.InfinityRaider.settlercraft.registry;


import com.InfinityRaider.settlercraft.SettlerCraft;
import com.InfinityRaider.settlercraft.api.v1.ISettlerCraftEntityRegistry;
import com.InfinityRaider.settlercraft.handler.ConfigurationHandler;
import com.InfinityRaider.settlercraft.reference.Reference;
import com.InfinityRaider.settlercraft.settlement.settler.EntitySettler;
import com.InfinityRaider.settlercraft.utility.BiomeHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureType;

import java.awt.*;

public class EntityRegistry implements ISettlerCraftEntityRegistry {
    private static final EntityRegistry INSTANCE = new EntityRegistry();

    public static EntityRegistry getInstance() {
        return INSTANCE;
    }

    private EntityRegistry() {}

    public static final String SETTLER = "entity.settler";

    public final int ID_SETTLER = 0;

    public void init() {
        //Settler
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
                entitySettlerClass(),
                SETTLER, ID_SETTLER,
                SettlerCraft.instance, 64, 1, true);
        net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(
                EntitySettler.class, new Color(8, 8, 255).getRGB(),
                new Color(51, 255, 247).getRGB());
        net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
                Reference.MOD_ID + "." + SETTLER,
                ConfigurationHandler.getInstance().settlerSpawnWeight, 1, 1,
                EnumCreatureType.CREATURE,
                BiomeHelper.getInstance().convertBiomeNamesList(ConfigurationHandler.getInstance().settlerSpawnBiomes));

    }

    @Override
    public Class<? extends EntityAgeable> entitySettlerClass() {
        return EntitySettler.class;
    }

    @Override
    public String entitySettlerId() {
        return  Reference.MOD_ID + "." + SETTLER;
    }
}
