package ru.crutch.interfaces.server.management;

import net.minecraft.world.chunk.Chunk;

public interface IMixinPlayerChunkMapEntry {
    void setChunk(Chunk chunk);
}
