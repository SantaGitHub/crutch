package ru.crutch.interfaces.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public interface IMixinInventoryCrafting {

    void setCurrentRecipe(IRecipe recipe);
    IRecipe getCurrentRecipe();
    ItemStack[] getContents();
    void onOpen(CraftHumanEntity who);
    InventoryType getInvType();
    void onClose(CraftHumanEntity who);
    List<HumanEntity> getViewers();
    org.bukkit.inventory.InventoryHolder getOwner();
    void setMaxStackSize(int size);
    Location getLocation();
}
