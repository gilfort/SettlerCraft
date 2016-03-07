package com.InfinityRaider.settlercraft.settlement;

import com.InfinityRaider.settlercraft.api.v1.*;
import com.InfinityRaider.settlercraft.network.MessageAddInhabitant;
import com.InfinityRaider.settlercraft.network.NetworkWrapperSettlerCraft;
import com.InfinityRaider.settlercraft.reference.Names;
import com.InfinityRaider.settlercraft.settlement.building.BuildingRegistry;
import com.InfinityRaider.settlercraft.settlement.building.BuildingTypeRegistry;
import com.InfinityRaider.settlercraft.utility.AbstractEntityFrozen;
import com.InfinityRaider.settlercraft.utility.ChunkCoordinates;
import com.InfinityRaider.settlercraft.utility.SettlementBoundingBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Settlement extends AbstractEntityFrozen implements ISettlement {
    private static final int BUILDING_CLEARANCE = 5;

    private int id;
    private ChunkCoordinates homeChunk;
    private EntityPlayer player;
    private String name;
    private SettlementBoundingBox settlementBoundingBox;

    private int nextBuildingId;
    private HashMap<Integer, ISettlementBuilding> buildings;
    private HashMap<IBuildingType, List<ISettlementBuilding>> buildingsPerType;

    private int populationCount;
    private String playerUUID;

    public Settlement(World world) {
        super(world);
        this.resetBuildings();
    }

    public Settlement(int id, World world, EntityPlayer player, BlockPos center, String name) {
        this(world);
        this.posX = center.getX() + 0.5;
        this.posY = center.getY() + 0.5;
        this.posZ = center.getZ() + 0.5;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        this.id = id;
        this.homeChunk = new ChunkCoordinates(world, center);
        this.player = player;
        this.playerUUID = player.getUniqueID().toString();
        this.name = name;
        this.settlementBoundingBox = new SettlementBoundingBox(center.add(0, -1, 0));
        populationCount = 1;
    }

    @Override
    public NBTTagCompound writeSettlementToNBT(NBTTagCompound tag) {
        tag.setInteger(Names.NBT.SLOT, id);
        tag.setString(Names.NBT.FIRST_NAME, name);
        tag.setString(Names.NBT.SURNAME, playerUUID);
        tag.setIntArray(Names.NBT.HOME, new int[]{homeChunk.x(), homeChunk.z(), homeChunk.dim()});
        BlockPos pos = settlementBoundingBox.getMinimumPosition();
        tag.setInteger(Names.NBT.X, pos.getX());
        tag.setInteger(Names.NBT.Y, pos.getY());
        tag.setInteger(Names.NBT.Z, pos.getZ());
        tag.setInteger(Names.NBT.X_SIZE, xSize());
        tag.setInteger(Names.NBT.Y_SIZE, ySize());
        tag.setInteger(Names.NBT.Z_SIZE, zSize());
        tag.setInteger(Names.NBT.COUNT, nextBuildingId);
        //tag.setTag(Names.NBT.BUILDINGS, getBuildingTagList());
        return tag;
    }

    /*
    private NBTTagList getBuildingTagList() {
        NBTTagList buildings = new NBTTagList();
        for(List<ISettlementBuilding> buildingForType : this.buildingsPerType.values()) {
            for (ISettlementBuilding building : buildingForType) {
                NBTTagCompound buildingTag = building.writeBuildingToNBT(new NBTTagCompound());
                buildingTag.setBoolean(Names.NBT.COMPLETED, building.isComplete());
                buildings.appendTag(buildingTag);
            }
        }
        return buildings;
    }
    */

    @Override
    public NBTTagCompound readSettlementFromNBT(NBTTagCompound tag) {
        this.id = tag.getInteger(Names.NBT.SLOT);
        this.name = tag.getString(Names.NBT.FIRST_NAME);
        this.playerUUID = tag.getString(Names.NBT.SURNAME);
        int[] homeCoords = tag.getIntArray(Names.NBT.HOME);
        this.homeChunk = new ChunkCoordinates(homeCoords[0], homeCoords[1], homeCoords[2]);
        int minX = tag.getInteger(Names.NBT.X);
        int minY = tag.getInteger(Names.NBT.Y);
        int minZ = tag.getInteger(Names.NBT.Z);
        int maxX = minX + tag.getInteger(Names.NBT.X_SIZE);
        int maxY = minY + tag.getInteger(Names.NBT.Y_SIZE);
        int maxZ = minZ + tag.getInteger(Names.NBT.Z_SIZE);
        this.settlementBoundingBox = new SettlementBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        this.nextBuildingId = tag.getInteger(Names.NBT.COUNT);
        //this.readBuildingsFromTagList(tag.getTagList(Names.NBT.BUILDINGS, 10));
        return tag;
    }

    /*
    private void readBuildingsFromTagList(NBTTagList list) {
        this.resetBuildings();
        for(int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tagAt = list.getCompoundTagAt(i);
            ISettlementBuilding building = tagAt.getBoolean(Names.NBT.COMPLETED)
                    ? new SettlementBuildingComplete(this.world())
                    : new SettlementBuildingIncomplete(this.world());
            building.readBuildingFromNBT(tagAt);
            this.addBuilding(building);
        }
    }
    */

    @Override
    public int id() {
        return id;
    }

    @Override
    public World world() {
        return getEntityWorld();
    }

    @Override
    public Chunk homeChunk() {
        return homeChunk.getChunk();
    }

    @Override
    public EntityPlayer mayor() {
        if(player == null) {
            player = world().getPlayerEntityByUUID(UUID.fromString(playerUUID));
        }
        return player;
    }

    @Override
    public boolean isMayor(EntityPlayer player) {
        if(player == null) {
            return false;
        }
        EntityPlayer mayor = mayor();
        if(mayor == null) {
            return playerUUID.equals(player.getUniqueID().toString());
        }
        return player.getUniqueID().equals(mayor().getUniqueID());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void rename(String name) {
        this.name = name;
    }

    @Override
    public ISettlementBuilding getBuildingFromId(int id) {
        return buildings.get(id);
    }

    @Override
    public List<ISettlementBuilding> getBuildings() {
        List<ISettlementBuilding> structuresList = new ArrayList<>();
        buildingsPerType.values().forEach(structuresList::addAll);
        return structuresList;
    }

    @Override
    public List<ISettlementBuilding> getBuildings(IBuildingType buildingType) {
        if(buildingsPerType.containsKey(buildingType)) {
            return buildingsPerType.get(buildingType);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean hasBuilding(IBuilding building) {
        if(building == null) {
            return false;
        }
        for(ISettlementBuilding built : buildingsPerType.get(building.buildingType())) {
            if(built.building().equals(building) && built.isComplete()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<IBuilding> getBuildableBuildings() {
        List<IBuilding> list = new ArrayList<>();
        List<ISettlementBuilding> townHalls = this.getBuildings(BuildingTypeRegistry.getInstance().buildingTypeTownHall());
        if(list.size() <= 0) {
            list.add(BuildingRegistry.getInstance().TOWN_HALL_1);
        }
        boolean complete = false;
        for(ISettlementBuilding townHall : townHalls) {
            if(townHall.isComplete()) {
                complete = true;
                break;
            }
        }
        if(!complete) {
            return list;
        }
        list.add(BuildingRegistry.getInstance().HOUSE_1);
        return list;
    }

    @Override
    public boolean canBuildNewBuilding(IBuilding building) {
        List<ISettlementBuilding> buildingsForType = buildingsPerType.get(building.buildingType());
        return buildingsForType.size() < building.buildingType().maximumBuildingCountPerSettlement(this);
    }

    @Override
    public void addBuilding(ISettlementBuilding building) {
        int id = nextBuildingId;
        nextBuildingId = nextBuildingId + 1;
        if(!world().isRemote) {
            building.assignIdAndAddToWorld(id);
        }
    }

    @Override
    public void removeBuilding(ISettlementBuilding building) {
        buildingsPerType.get(building.building().buildingType()).remove(building);
    }

    @Override
    public void addInhabitant(ISettler settler) {
        if(settler.settlement() == null) {
            settler.setSettlement(this);
            this.populationCount = populationCount +1;
        }
        if(!world().isRemote) {
            NetworkWrapperSettlerCraft.getInstance().sendToAll(new MessageAddInhabitant(this, settler.getEntityImplementation()));
        }
    }

    @Override
    public List<ISettler> getSettlementInhabitants() {
        List<ISettler> list = new ArrayList<>();
        for(ISettlementBuilding building : getBuildings()) {
            list.addAll(building.inhabitants());
        }
        return list;
    }

    @Override
    public int population() {
        return populationCount;
    }

    @Override
    public boolean isWithinSettlementBounds(double x, double y, double z) {
        return settlementBoundingBox.isWithinBounds(x, y, z);
    }

    @Override
    public int xSize() {
        return settlementBoundingBox.xSize();
    }

    @Override
    public int ySize() {
        return settlementBoundingBox.ySize();
    }

    @Override
    public int zSize() {
        return settlementBoundingBox.zSize();
    }

    @Override
    public SettlementBoundingBox getBoundingBox() {
        return settlementBoundingBox;
    }

    @Override
    public double calculateDistanceSquaredToSettlement(BlockPos pos) {
        return this.getDistanceSqToCenter(pos);
    }

    @Override
    public void onBuildingUpdated(ISettlementBuilding building) {
        this.buildings.put(building.id(), building);
        IBuildingType type = building.building().buildingType();
        if(!buildingsPerType.containsKey(type)) {
            this.buildingsPerType.put(type, new ArrayList<>());
        }
        this.buildingsPerType.get(type).add(building);
        IBoundingBox buildingBox = building.getBoundingBox().copy();
        buildingBox.expandToFit(buildingBox.getMaximumPosition().add(BUILDING_CLEARANCE, BUILDING_CLEARANCE, BUILDING_CLEARANCE));
        buildingBox.expandToFit(buildingBox.getMinimumPosition().add(-BUILDING_CLEARANCE, -BUILDING_CLEARANCE, -BUILDING_CLEARANCE));
        this.settlementBoundingBox.expandToFit(buildingBox);
    }

    private void resetBuildings() {
        this.nextBuildingId = 0;
        this.buildings = new HashMap<>();
        this.buildingsPerType = new HashMap<>();
        for(IBuildingType type : BuildingTypeRegistry.getInstance().allBuildingTypes()) {
            buildingsPerType.put(type, new ArrayList<>());
        }
    }

    @Override
    public void update() {

    }

    @Override
    protected void onEntitySpawned() {
        SettlementHandler.getInstance().onSettlementLoaded(this);
    }

    @Override
    protected void onChunkLoaded() {
        SettlementHandler.getInstance().onSettlementLoaded(this);
    }

    @Override
    protected void readDataFromNBT(NBTTagCompound tag) {
        readSettlementFromNBT(tag);
    }

    @Override
    protected void writeDataToNBT(NBTTagCompound tag) {
        writeSettlementToNBT(tag);
    }
}
