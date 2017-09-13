package ru.crutch.interfaces.server.management;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public interface IMixinPlayerChunkMapEntry {
    void setChunk(Chunk chunk);
    List<EntityPlayerMP> getPlayers();
}
