package ru.crutch.mixin.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.player.IMixinEntityPlayerMP;
import ru.crutch.interfaces.inventory.IMixinInventoryCrafting;
import ru.crutch.inventory.ICBInventory;

import java.util.List;

@Mixin(InventoryCrafting.class)
public abstract class MixinInventoryCrafting  implements IMixinInventoryCrafting, ICBInventory{

    @Shadow private ItemStack[] stackList;
    @Shadow private int inventoryWidth;
    @Shadow private int inventoryHeight;
    @Shadow private Container eventHandler;

    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    public IRecipe currentRecipe;
    public ICBInventory resultInventory;
    private EntityPlayer owner;
    private int maxStack = MAX_STACK;

    @Override
    public void setCurrentRecipe(IRecipe recipe) {
        this.currentRecipe = recipe;
    }

    @Override
    public IRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }


    @Override
    public ItemStack[] getContents() {
        return this.stackList;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public InventoryType getInvType() {
        return stackList.length == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return ((owner == null) ? null : ((IMixinEntityPlayerMP) owner).getBukkitEntity());
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
        ((ICBInventory)resultInventory).setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return ((IMixinEntityPlayerMP) owner).getBukkitEntity().getLocation();
    }

    public MixinInventoryCrafting(Container container, int i, int j, EntityPlayer player) {
        int size = i * j;
        this.stackList = new ItemStack[size];
        this.eventHandler = container;
        this.inventoryWidth = i;
        this.inventoryHeight = j;
        this.owner = player;
    }

}
