package ru.crutch.mixin.inventory;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.crutch.interfaces.item.crafting.IMixinFurnaceRecipes;

import javax.annotation.Nullable;
import java.util.Map;
@Mixin(FurnaceRecipes.class)
public class MixinFurnaceRecipes implements IMixinFurnaceRecipes {

    @Shadow
    public Map<ItemStack, ItemStack> smeltingList = Maps.<ItemStack, ItemStack>newHashMap();
    public Map<ItemStack,ItemStack> customRecipes = Maps.newHashMap();
    public int customRecipesSize = 0;
    public Map<ItemStack,Float> customExperience = Maps.newHashMap();

    @Override
    public Map<ItemStack, ItemStack> getCustomRecipes(){
        return this.customRecipes;
    }
    @Override
    public Map<ItemStack, Float> getCustomExperience(){
        return this.customExperience;
    }

    @Override
    public void registerRecipe(ItemStack itemstack, ItemStack itemstack1, float f) {
       	this.customRecipes.put(itemstack, itemstack1);
       	customRecipesSize++;
    }

    @Nullable
    @Redirect(method = "getSmeltingResult", at=@At("INVOKE"))
    public ItemStack onGetSmeltingResult(ItemStack stack) {
        if(customRecipesSize > 0)
            for (Map.Entry<ItemStack, ItemStack> entry : this.customRecipes.entrySet())
            {
                if (this.compareItemStacks(stack, (ItemStack)entry.getKey()))
                {
                    return (ItemStack)entry.getValue();
                }
            }
        for (Map.Entry<ItemStack, ItemStack> entry : this.smeltingList.entrySet())
        {
            if (this.compareItemStacks(stack, (ItemStack)entry.getKey()))
            {
                return (ItemStack)entry.getValue();
            }
        }

        return null;
    }
    @Override
    public boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }
}
