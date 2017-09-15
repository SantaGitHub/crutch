package ru.crutch.interfaces.server.management;

import net.minecraft.server.management.PlayerChunkMapEntry;

public interface IMixinPlayerChunkMap {

    boolean isChunkInUse(int x, int z);

    PlayerChunkMapEntry getEntry(int x, int z);
}
