package com.InfinityRaider.settlercraft.settlement.building.decorative;

import com.InfinityRaider.settlercraft.api.v1.ISettlement;
import net.minecraft.entity.player.EntityPlayer;

public class BuildingWaterWell extends BuildingDecorative {
    public BuildingWaterWell() {
        super("water_well");
    }

    @Override
    public boolean canBuild(EntityPlayer player, ISettlement settlement) {
        return true;
    }
}
