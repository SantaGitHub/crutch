package ru.crutch.interfaces.world;

import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.craftbukkit.CraftChunk;

public interface IMixinChunk {
    CraftChunk getBukkitChunk();


    void populateChunk(IChunkGenerator generator);

    void setBukkitChunk(CraftChunk bukkitChunk);

    void loadNearby(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, boolean newChunk);

    boolean isTerrainPopulated();

    void setNeighborLoaded(final int x, final int z);

    void setNeighborUnloaded(final int x, final int z);

    int getNeighbors();
}
