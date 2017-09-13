package ru.crutch.interfaces.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;


public interface IMixinWorld {

    CraftWorld getWorld();

    CraftServer getServer();

    boolean setTypeId(int x, int y, int z, int typeId);

    boolean setTypeIdAndData(int x, int y, int z, int typeId, int data);

    int getTypeId(int x, int y, int z);

    boolean setTypeAndData(int x, int y, int z, Block block, int data, int flag);

    boolean setData(int x, int y, int z, int data, int flag);

    int getData(int x, int y, int z);

    Block getType(int x, int y, int z);

}
