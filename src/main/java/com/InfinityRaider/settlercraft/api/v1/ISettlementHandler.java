package com.InfinityRaider.settlercraft.api.v1;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

/**
 * Interface used to interact with the settlement handler,
 * the instance of this interface can be retrieved via api.getSettlementHandler()
 */
public interface ISettlementHandler {
    /**
     * Gets a settlement by its id
     * @param world the world object
     * @param id the id of the settlement
     * @return the settlement with the requested id or null if there is no settlement with this id
     */
    ISettlement getSettlement(World world, int id);

    /**
     * Gets the settlement at the given coordinates in the world, may return null if there is no settlement here
     * (Slow method, if you call this often, cache it instead, settlements don't go anywhere)
     * @param world the world to look for a settlement in
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the settlement at the given position or null if there is no settlement
     */
    ISettlement getSettlementForPosition(World world, double x, double y, double z);

    /**
     * Gets the settlement which has the argument as its home chunk
     * @param chunk the home chunk to get the settlement for
     * @return the settlement in the chunk, or null if there is none
     */
    ISettlement getSettlementForChunk(Chunk chunk);

    /**
     * Gets the settlement nearest to the passed position
     * @param world world for the settlement
     * @param pos position to look for a settlement nearby
     * @return the closest settlement, or null if there are no settlements in the world
     */
    ISettlement getNearestSettlement(World world, BlockPos pos);

    /**
     * Gets a list of all settlements in a world
     * @param world the World object
     * @return a list containing all the settlements for the given world, may be empty but should never be null
     */
    List<ISettlement> getSettlementsForWorld(World world);

    /**
     * Checks if all the requirements are met to create a new settlement with the player as mayor at his current position
     * @param player the player creating a new settlement
     * @return if a settlement can be made here
     */
    boolean canCreateSettlementAtCurrentPosition(EntityPlayer player);

    /**
     * Tries to start a new settlement at the player's current position, the player will be the mayor of the new settlement
     * This method can fail if the location is too close to an existing settlement, the player's world object is remote, the argument is null
     * or the player does not meet the requirements to create a new settlement here.
     * If the method fails, it will return null instead of the newly created settlement.
     * This method internally calls onCreateSettlementAtCurrentPosition(player)
     *
     * @param player The player creating the settlement with the settler, this will also be the mayor
     * @param style Building style for this settlement
     * @return the newly created ISettlement object, or null if the player can't make a settlement here.
     */
    ISettlement startNewSettlement(EntityPlayer player, IBuildingStyle style);

    /**
     * Sometimes a settler has to perform actions which usually only players can do,
     * it is possible with this method to get a fake player implementation for the settler
     * @param settler settler to get a fake player implementation for
     * @return fake player for the settler
     */
    FakePlayer getFakePlayerForSettler(ISettler settler);
}
