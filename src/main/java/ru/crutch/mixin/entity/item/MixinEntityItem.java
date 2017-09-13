package ru.crutch.mixin.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.item.IMixinEntityItem;
@Mixin(net.minecraft.entity.item.EntityItem.class)
public class MixinEntityItem implements IMixinEntityItem {


    @Shadow
    int delayBeforeCanPickup;

    @Override
    public void setdelayBeforeCanPickup(int i) {
        delayBeforeCanPickup = i;
    }

    @Override
    public int getdelayBeforeCanPickup() {
        return this.delayBeforeCanPickup;
    }
}
