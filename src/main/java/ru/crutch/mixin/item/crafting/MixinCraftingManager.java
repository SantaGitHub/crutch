package ru.crutch.mixin.item.crafting;

import net.minecraft.item.crafting.CraftingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.crutch.interfaces.item.crafting.IMixinCraftingManager;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager implements IMixinCraftingManager {


    public MixinCraftingManager(){

    }

}
