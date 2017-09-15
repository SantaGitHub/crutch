package ru.crutch.mixin.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.item.EntityItem;
import ru.crutch.interfaces.entity.item.IMixinEntityItem;
import ru.crutch.mixin.entity.MixinEntity;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends MixinEntity implements IMixinEntityItem {

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
