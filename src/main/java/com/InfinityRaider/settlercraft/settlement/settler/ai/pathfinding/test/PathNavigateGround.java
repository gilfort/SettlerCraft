package com.InfinityRaider.settlercraft.settlement.settler.ai.pathfinding.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGround extends PathNavigate {
    private boolean shouldAvoidSun;

    public PathNavigateGround(EntityLiving entity, World world) {
        super(entity, world);
    }

    protected PathFinder getPathFinder() {
        this.setNodeProcessor(new WalkNodeProcessor());
        this.getNodeProcessor().setCanEnterDoors(true);
        return new PathFinder(this.getNodeProcessor());
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate() {
        return this.theEntity.onGround || this.getCanSwim() && this.isInLiquid() || this.theEntity.isRiding();
    }

    protected Vec3d getEntityPosition() {
        return new Vec3d(this.theEntity.posX, (double)this.getPathablePosY(), this.theEntity.posZ);
    }

    /**
     * Returns path to given BlockPos
     */
    public PathEntity getPathToPos(BlockPos pos) {
        if (this.worldObj.getBlockState(pos).getMaterial() == Material.air) {
            BlockPos blockpos;
            //get the position of the first block which is not air, downwards
            for (blockpos = pos.down(); blockpos.getY() > 0 && this.worldObj.getBlockState(blockpos).getMaterial() == Material.air; blockpos = blockpos.down()) {}
            if (blockpos.getY() > 0) {
                return super.getPathToPos(blockpos.up());
            }
            //if none is found, get the position of the first block which is not air, upwards
            while (blockpos.getY() < this.worldObj.getHeight() && this.worldObj.getBlockState(blockpos).getMaterial() == Material.air) {
                blockpos = blockpos.up();
            }
            pos = blockpos;
        }
        if (!this.worldObj.getBlockState(pos).getMaterial().isSolid()) {
            return super.getPathToPos(pos);
        } else {
            BlockPos blockPos1;
            for (blockPos1 = pos.up(); blockPos1.getY() < this.worldObj.getHeight() && this.worldObj.getBlockState(blockPos1).getMaterial().isSolid(); blockPos1 = blockPos1.up()) {}
            return super.getPathToPos(blockPos1);
        }
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public PathEntity getPathToEntityLiving(Entity entityIn) {
        BlockPos blockpos = new BlockPos(entityIn);
        return this.getPathToPos(blockpos);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    private int getPathablePosY() {
        if (this.theEntity.isInWater() && this.getCanSwim()) {
            int y = (int)this.theEntity.getEntityBoundingBox().minY;
            Block block = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), y, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
            int j = 0;
            while (block == Blocks.flowing_water || block == Blocks.water) {
                ++y;
                block = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), y, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
                ++j;
                if (j > 16) {
                    return (int)this.theEntity.getEntityBoundingBox().minY;
                }
            }
            return y;
        } else {
            return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D);
        }
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath() {
        super.removeSunnyPath();
        for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            IBlockState iblockstate = this.worldObj.getBlockState(new BlockPos(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord));
            Block block = iblockstate.getBlock();
            if (block == Blocks.cauldron) {
                this.currentPath.func_186309_a(i, pathpoint.copyToNewPathPointWithCoordinates(pathpoint.xCoord, pathpoint.yCoord + 1, pathpoint.zCoord));
                if (pathpoint1 != null && pathpoint.yCoord >= pathpoint1.yCoord) {
                    this.currentPath.func_186309_a(i + 1, pathpoint1.copyToNewPathPointWithCoordinates(pathpoint1.xCoord, pathpoint.yCoord + 1, pathpoint1.zCoord));
                }
            }
        }
        if (this.shouldAvoidSun) {
            if (this.worldObj.canSeeSky(new BlockPos(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ)))) {
                return;
            }
            for (int j = 0; j < this.currentPath.getCurrentPathLength(); ++j) {
                PathPoint pathpoint2 = this.currentPath.getPathPointFromIndex(j);
                if (this.worldObj.canSeeSky(new BlockPos(pathpoint2.xCoord, pathpoint2.yCoord, pathpoint2.zCoord))) {
                    this.currentPath.setCurrentPathLength(j - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor_double(posVec31.xCoord);
        int j = MathHelper.floor_double(posVec31.zCoord);
        double d0 = posVec32.xCoord - posVec31.xCoord;
        double d1 = posVec32.zCoord - posVec31.zCoord;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;
            if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                return false;
            } else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)i - posVec31.xCoord;
                double d7 = (double)j - posVec31.zCoord;
                if (d0 >= 0.0D) {
                    ++d6;
                }
                if (d1 >= 0.0D) {
                    ++d7;
                }
                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor_double(posVec32.xCoord);
                int j1 = MathHelper.floor_double(posVec32.zCoord);
                int k1 = i1 - i;
                int l1 = j1 - j;
                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }
                    if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;
        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        } else {
            for (int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double)k + 0.5D - vec31.xCoord;
                    double d1 = (double)l + 0.5D - vec31.zCoord;
                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
                        PathNodeType pathnodetype = this.getNodeProcessor().getPathNodeType(this.worldObj, k, y - 1, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);
                        if (pathnodetype == PathNodeType.WATER) {
                            return false;
                        }
                        if (pathnodetype == PathNodeType.LAVA) {
                            return false;
                        }
                        if (pathnodetype == PathNodeType.OPEN) {
                            return false;
                        }
                        pathnodetype = this.getNodeProcessor().getPathNodeType(this.worldObj, k, y, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);
                        float f = this.theEntity.getPathPriority(pathnodetype);
                        if (f < 0.0F || f >= 8.0F) {
                            return false;
                        }
                        if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position.
     */
    private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
            double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.xCoord;
            double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.zCoord;
            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D) {
                Block block = this.worldObj.getBlockState(blockpos).getBlock();
                if (!block.isPassable(this.worldObj, blockpos)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setBreakDoors(boolean canBreakDoors) {
        this.getNodeProcessor().setCanBreakDoors(canBreakDoors);
    }

    public void setEnterDoors(boolean par1) {
        this.getNodeProcessor().setCanEnterDoors(par1);
    }

    public boolean getEnterDoors() {
        return this.getNodeProcessor().getCanEnterDoors();
    }

    public void setCanSwim(boolean canSwim) {
        this.getNodeProcessor().setCanSwim(canSwim);
    }

    public boolean getCanSwim() {
        return this.getNodeProcessor().getCanSwim();
    }

    public void setAvoidSun(boolean par1) {
        this.shouldAvoidSun = par1;
    }
}