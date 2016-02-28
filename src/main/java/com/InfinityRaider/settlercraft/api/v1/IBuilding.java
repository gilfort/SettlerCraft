package com.InfinityRaider.settlercraft.api.v1;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This interface is used to construct buildings, only one instance per building is created and registered
 *
 * If you are creating a whole new building type, the IBuilding objects for that type will be automatically registered (assuming your implementation is correct)
 * If you want to add a new building to an existing building type, retrieve that building type from the IBuildingTypeRegistry (which can be retrieved via the APIv1)
 * and call iBuildingType.registerBuilding(IBuilding building)
 */
public interface IBuilding {
    /**
     * @return a name unique to this building, this should be an unlocalized string
     */
    String name();

    /**
     * @return The building type this building belongs to
     */
    IBuildingType buildingType();

    /**
     * This method is called when the player wants to build this building in his settlement,
     * use it to check if all the required prerequisites are met
     *
     * @param player the player owning the settlement
     * @param settlement the settlement
     * @return if the player is allowed to build this building in the settlement
     */
    boolean canBuild(EntityPlayer player, ISettlement settlement);

    /**
     * Some buildings can be upgraded
     * @param building
     * @return
     */
    boolean canBeUpgradedFromBuilding(ISettlementBuilding building);

    /**
     * Every building has an inventory, this is the default starting inventory of a newly created building and will be used further.
     * It is important that a new IInventory object is returned from this method, or all buildings of this instance will share the same inventory.
     *
     * @param previousBuilding if this building is built as an upgrade from a previous stage, it is passed as an argument, can be null
     * @return a new IInventory instance for the starting inventory of the new building
     */
    IInventory getStartingInventory(@Nullable ISettlementBuilding previousBuilding);

    /**
     * This method is used to read json schematics for the buildings, example:
     * new ResourceLocation("settlercraft", "buildings/house/house1") will be converted to "assets/settlercraft/buildings/house/house1.json"
     *
     * Schematics can be created by building the structure in a world and then using the schematic creator item to export the building as a json to
     * the file specified in the config.
     *
     * @return a ResourceLocation containing the path to the json file defining this building
     */
    ResourceLocation schematicLocation();

    /**
     * @return the maximum number of settlers living in this building
     */
    int maxInhabitants();

    /**
     * Checks if a settler can work here, this is called when a settler is idle and needs somewhere to work
     * @param building the ISettlementBuilding object where this is built
     * @param settler the settler wanting to work here
     * @return if the settler can work here
     */
    boolean canSettlerWorkHere(ISettlementBuilding building, ISettler settler);

    ITask getTaskForVillager(ISettlementBuilding building, ISettler settler);

    /**
     * @return if this building needs to receive ticks
     */
    boolean needsUpdateTicks();

    /**
     * Called every tick, only if true is returned from needsUpdateTicks()
     * @param building the ISettlementBuilding object where this is built
     */
    void onUpdateTick(ISettlementBuilding building);
}
