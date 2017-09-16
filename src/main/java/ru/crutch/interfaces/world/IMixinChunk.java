package ru.crutch.interfaces.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.craftbukkit.CraftChunk;

import java.util.Map;

public interface IMixinChunk {
    CraftChunk getBukkitChunk();


    void populateChunk(IChunkGenerator generator);

    void setBukkitChunk(CraftChunk bukkitChunk);

    void loadNearby(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, boolean newChunk);

    Map<BlockPos, TileEntity> getChunkTileEntityMap();


    boolean isTerrainPopulated();

    void setNeighborLoaded(final int x, final int z);

    void setNeighborUnloaded(final int x, final int z);

    int getNeighbors();
}
