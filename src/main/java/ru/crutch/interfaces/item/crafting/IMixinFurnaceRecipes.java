package ru.crutch.interfaces.item.crafting;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

public interface IMixinFurnaceRecipes {
    Map<ItemStack, ItemStack> getCustomRecipes();

    Map<ItemStack, Float> getCustomExperience();

    void registerRecipe(ItemStack itemstack, ItemStack itemstack1, float f);
    ItemStack onGetSmeltingResult(ItemStack stack);

    boolean compareItemStacks(ItemStack stack1, ItemStack stack2);
}
