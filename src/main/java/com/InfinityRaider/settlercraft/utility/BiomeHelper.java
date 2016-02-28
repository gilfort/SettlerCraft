package com.InfinityRaider.settlercraft.utility;

import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeHelper {
    private static final BiomeHelper INSTANCE = new BiomeHelper();

    public static BiomeHelper getInstance() {
        return INSTANCE;
    }

    private BiomeHelper() {}

    public String[] getBiomeList() {
        List<String> list = BiomeGenBase.explorationBiomesList.stream().map(biome -> biome.biomeName).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    public BiomeGenBase[] convertBiomeNamesList(String[] names) {
        List<BiomeGenBase> list = new ArrayList<>();
        for(String name : names) {
            BiomeGenBase biome = BiomeGenBase.BIOME_ID_MAP.get(name);
            if(biome != null) {
                list.add(biome);
            }
        }
        return list.toArray(new BiomeGenBase[list.size()]);
    }
}
