package com.InfinityRaider.settlercraft.settlement.building.house;

import com.InfinityRaider.settlercraft.api.v1.*;
import com.InfinityRaider.settlercraft.settlement.building.BuildingTypeRegistry;
import com.InfinityRaider.settlercraft.settlement.building.BuildingBase;
import net.minecraft.entity.player.EntityPlayer;

public class BuildingHouse extends BuildingBase {
    public BuildingHouse() {
        super("house1");
    }

    @Override
    public IBuildingType buildingType() {
        return BuildingTypeRegistry.getInstance().buildingTypeHouse();
    }

    @Override
    public boolean canBuild(EntityPlayer player, ISettlement settlement) {
        return settlement.getBuildings(BuildingTypeRegistry.getInstance().buildingTypeTownHall()).size() > 0;
    }

    @Override
    public IInventorySerializable getStartingInventory() {
        return null;
    }

    @Override
    public int maxInhabitants() {
        return 2;
    }

    @Override
    public boolean canSettlerWorkHere(ISettlementBuilding building, ISettler settler) {
        return false;
    }

    @Override
    public ITask getTaskForVillager(ISettlementBuilding building, ISettler settler) {
        return null;
    }

    @Override
    public boolean needsUpdateTicks() {
        return false;
    }

    @Override
    public void onUpdateTick(ISettlementBuilding building) {}
}
