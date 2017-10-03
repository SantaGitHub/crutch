package ru.crutch.mixin.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.crutch.crafting.ICBRecipe;
import ru.crutch.interfaces.inventory.IMixinInventoryCrafting;
import ru.crutch.interfaces.item.crafting.IMixinCraftingManager;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager implements IMixinCraftingManager {


    @Shadow
    public List<IRecipe> recipes;

    public ICBRecipe lastRecipe;
    public org.bukkit.inventory.InventoryView lastCraftView;

    public MixinCraftingManager(){

    }

    @Nullable @Redirect(method = "findMatchingRecipe", at=@At("INVOKE"))
    public ItemStack onFindMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                ((IMixinInventoryCrafting) craftMatrix).setCurrentRecipe(irecipe);
                ItemStack result = irecipe.getCraftingResult(craftMatrix);
                if(irecipe instanceof ICBRecipe)
                    return CraftEventFactory.callPreCraftEvent(craftMatrix, result, lastCraftView, false);
                return result;
            }
        }
        ((IMixinInventoryCrafting) craftMatrix).setCurrentRecipe(null);
        return null;
    }

}
