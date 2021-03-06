package com.InfinityRaider.settlercraft.settlement.settler.dialogue;

import com.InfinityRaider.settlercraft.api.v1.IBuilding;
import com.InfinityRaider.settlercraft.api.v1.IDialogueOption;
import com.InfinityRaider.settlercraft.api.v1.ISettler;
import com.InfinityRaider.settlercraft.item.ItemBuildingPlanner;
import com.InfinityRaider.settlercraft.registry.ItemRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.List;

public class DialogueOptionSelectBuilding extends DialogueOptionBase {
    private final IBuilding building;

    public DialogueOptionSelectBuilding(EntityPlayer player, ISettler settler, IBuilding building) {
        super(player, settler);
        this.building = building;
    }

    @Override
    public List<IDialogueOption> getDialogueOptions(EntityPlayer player, ISettler settler) {
        return ImmutableList.of();
    }

    @Override
    public boolean onDialogueOptionSelected(EntityPlayer player, ISettler settler) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player, ISettler settler) {
        ItemBuildingPlanner planner = ItemRegistry.getInstance().itemBuildingPlanner;
        ItemStack stack = new ItemStack(planner, 1, 0);
        planner.setBuilding(stack, building);
        planner.setSettlement(stack, settler.settlement());
        player.inventory.addItemStackToInventory(stack);
    }

    @Override
    public List<String> getLocalizedSettlerTextString() {
        List<String> list = new ArrayList<>();
        list.add(I18n.translateToLocal(getDiscriminator() + "planBuild"));
        return list;
    }

    @Override
    public List<String> getLocalizedPlayerTextString() {
        List<String> list = new ArrayList<>();
        list.add(I18n.translateToLocal(getDiscriminator() + "buildNew"));
        list.add(I18n.translateToLocal(building.name()));
        return list;
    }
}
