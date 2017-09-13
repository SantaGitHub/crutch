package ru.crutch.tileentity;

import net.minecraft.tileentity.TileEntityLockableLoot;
import ru.crutch.interfaces.world.IMixinWorld;
import ru.crutch.inventory.ICBInventory;

public abstract class CBTileEntityLockableLoot extends TileEntityLockableLoot implements ICBInventory {
	// CraftBukkit start
    @Override
    public org.bukkit.Location getLocation() {
    	return new org.bukkit.Location(((IMixinWorld)world).getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
    // CraftBukkit end
}
