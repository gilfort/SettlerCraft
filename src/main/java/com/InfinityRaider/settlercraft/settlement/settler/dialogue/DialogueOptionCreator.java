package com.InfinityRaider.settlercraft.settlement.settler.dialogue;

import com.InfinityRaider.settlercraft.api.v1.IDialogueOption;
import com.InfinityRaider.settlercraft.api.v1.IDialogueOptionCreator;
import com.InfinityRaider.settlercraft.api.v1.ISettlementBuilding;
import com.InfinityRaider.settlercraft.api.v1.ISettler;
import net.minecraft.entity.player.EntityPlayer;

public class DialogueOptionCreator implements IDialogueOptionCreator {
    private static final DialogueOptionCreator INSTANCE = new DialogueOptionCreator();

    public static DialogueOptionCreator getInstance() {
        return INSTANCE;
    }

    private DialogueOptionCreator() {}

    @Override
    public IDialogueOption dialogueOptionRequestName(EntityPlayer player, ISettler settler, IDialogueOption previous) {
        return new DialogueOptionRequestName(player, settler, previous);
    }

    @Override
    public IDialogueOption dialogueOptionEndConversation(EntityPlayer player, ISettler settler) {
        return new DialogueOptionEndConversation(player, settler);
    }

    @Override
    public IDialogueOption dialogueOptionFollowPlayer(EntityPlayer player, ISettler settler, boolean doFollow) {
        return new DialogueOptionFollow(player, settler, doFollow);
    }

    @Override
    public IDialogueOption dialogueOptionShowInventory(EntityPlayer player, ISettler settler) {
        return new DialogueOptionShowInventory(player, settler);
    }

    @Override
    public IDialogueOption dialogueOptionDescribeTask(EntityPlayer player, ISettler settler, IDialogueOption previous) {
        return new DialogueOptionDescribeTask(player, settler, previous);
    }

    @Override
    public IDialogueOption dialogueOptionAssignHouse(EntityPlayer player, ISettler settler, ISettlementBuilding building) {
        return new DialogueOptionAssignHouse(player, settler, building);
    }

    @Override
    public IDialogueOption dialogueOptionAssignWorkplace(EntityPlayer player, ISettler settler, ISettlementBuilding building) {
        return new DialogueOptionAssignWorkplace(player, settler, building);
    }
}
