package ru.crutch.tileentity;

import net.minecraft.tileentity.TileEntityLockableLoot;
import ru.crutch.inventory.ICBInventory;

public abstract class CBTileEntityLockableLoot extends TileEntityLockableLoot implements ICBInventory {
	// CraftBukkit start
    @Override
    public org.bukkit.Location getLocation() {
    	return new org.bukkit.Location(worldObj.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
    // CraftBukkit end
}
