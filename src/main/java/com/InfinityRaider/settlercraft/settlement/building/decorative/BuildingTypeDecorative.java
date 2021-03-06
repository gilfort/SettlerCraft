package com.InfinityRaider.settlercraft.settlement.building.decorative;

import com.InfinityRaider.settlercraft.api.v1.ISettlement;
import com.InfinityRaider.settlercraft.settlement.building.BuildingTypeBase;

public class BuildingTypeDecorative extends BuildingTypeBase {
    public BuildingTypeDecorative() {
        super("decorative");
    }

    @Override
    public int maximumBuildingCountPerSettlement(ISettlement settlement) {
        return settlement.getBuildings(this).size() + 1;
    }
}
