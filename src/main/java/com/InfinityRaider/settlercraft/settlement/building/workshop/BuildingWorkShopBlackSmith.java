package com.InfinityRaider.settlercraft.settlement.building.workshop;

import com.InfinityRaider.settlercraft.api.v1.*;
import net.minecraft.entity.player.EntityPlayer;

public class BuildingWorkShopBlackSmith extends BuildingWorkShop {
    public BuildingWorkShopBlackSmith() {
        super("workshop_blacksmith");
    }

    @Override
    public boolean canBuild(EntityPlayer player, ISettlement settlement) {
        return settlement.tier() >= 2;
    }

    @Override
    public boolean canSettlerWorkHere(ISettlementBuilding building, ISettler settler) {
        return false;
    }

    @Override
    public ITask getTaskForSettler(ISettlementBuilding building, ISettler settler) {
        return null;
    }

    @Override
    public boolean needsUpdateTicks() {
        return false;
    }

    @Override
    public void onUpdateTick(ISettlementBuilding building) {

    }
}
