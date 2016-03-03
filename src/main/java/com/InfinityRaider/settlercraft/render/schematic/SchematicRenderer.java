package com.InfinityRaider.settlercraft.render.schematic;

import com.InfinityRaider.settlercraft.api.v1.IBuilding;
import com.InfinityRaider.settlercraft.api.v1.ISettlement;
import com.InfinityRaider.settlercraft.item.ItemBuildingPlanner;
import com.InfinityRaider.settlercraft.utility.LogHelper;
import com.InfinityRaider.settlercraft.utility.SettlementBoundingBox;
import com.InfinityRaider.settlercraft.utility.schematic.Schematic;
import com.InfinityRaider.settlercraft.utility.schematic.SchematicReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class SchematicRenderer {
    private static final SchematicRenderer INSTANCE = new SchematicRenderer();

    public static SchematicRenderer getInstance() {
        return INSTANCE;
    }

    private SchematicWorld currentSchematic;
    private String name = "";

    private SchematicRenderer() {}

    public void doRender(WorldRenderer renderer) {
        if(this.hasSchematic()) {
            for(int x = 0; x < currentSchematic.sizeX(); x++) {
                for(int y = 0; y < currentSchematic.sizeY(); y++) {
                    for(int z = 0; z < currentSchematic.sizeZ(); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(currentSchematic.getBlockState(pos), pos, currentSchematic, renderer);
                    }
                }
            }
        }
    }

    public void setSchematicFromStack(ItemStack stack, ItemBuildingPlanner planner) {
        IBuilding building = planner.getBuilding(stack);
        if(building == null) {
            this.currentSchematic = null;
            this.name = "";
            return;
        }
        ISettlement settlement = planner.getSettlement(stack);
        if(settlement == null || !settlement.isMayor(Minecraft.getMinecraft().thePlayer)) {
            this.currentSchematic = null;
            this.name = "";
            return;
        }
        if(building.name().equals(this.name)) {
            return;
        }
        setCurrentSchematic(building);
    }

    public void setCurrentSchematic(IBuilding building) {
        Schematic schematic;
        try {
            schematic = SchematicReader.getInstance().deserialize(building.schematicLocation());
        } catch (IOException e) {
            LogHelper.printStackTrace(e);
            return;
        }
        this.currentSchematic = new SchematicWorld(Minecraft.getMinecraft().theWorld, new BlockPos(0, 0, 0), schematic, 0);
        this.name = building.name();
    }

    public SchematicRenderer setOrigin(BlockPos pos) {
        if(hasSchematic()) {
            currentSchematic.setOrigin(pos);
        }
        return this;
    }

    public SettlementBoundingBox getBoundingBox() {
        if(hasSchematic()) {
            return currentSchematic.getBoundingBox();
        }
        return null;
    }

    public boolean hasSchematic() {
        return currentSchematic != null;
    }

}