package com.InfinityRaider.settlercraft.api.v1;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Helper interface to access the items added to Minecraft by SettlerCraft
 * The instance of this can be retrieved via APIv1.getItemRegistry()
 */
public interface ISettlerCraftItemRegistry {
    /**
     * @return the Creative Tab for SettlerCraft
     */
    CreativeTabs creativeTabSettlerCraft();

    /**
     * @return the Item instance for the schematic creator
     */
    Item itemSchematicCreator();

    /**
     * @return the Item instance for the debugger
     */
    Item itemDebugger();

    /**
     * @return the Item instance for the building planner
     */
    Item itemBuildingPlanner();

    /**
     * @return the IItemBuildingPlanner implementation for the building planner
     */
    IItemBuildingPlanner getBuildingPlanner();
}
