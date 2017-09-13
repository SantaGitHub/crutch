package ru.crutch.mixin.entity.projectile;

import com.google.common.collect.Sets;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.projectile.IMixinEntityTippedArrow;

import java.util.Set;

@Mixin(net.minecraft.entity.projectile.EntityTippedArrow.class)
public abstract class MixinEntityTippedArrow extends EntityArrow implements IMixinEntityTippedArrow {

    @Shadow
    private static DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityTippedArrow.class, DataSerializers.VARINT);
    private PotionType potion;
    private Set<PotionEffect> customPotionEffects = Sets.<PotionEffect>newHashSet();
    public MixinEntityTippedArrow(World worldIn) {
        super(worldIn);
    }

    @Override
    public void refreshEffects() {
        this.dataManager.set(this.COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    @Override
    public String getType() {
        return (PotionType.REGISTRY.getNameForObject(this.potion)).toString();
    }


    @Override
    public void setType(String string) {
        this.potion = PotionType.REGISTRY.getObject(new ResourceLocation(string));
        this.dataManager.set(this.COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    @Override
    public boolean isTipped() {
        return !(this.customPotionEffects.isEmpty() && this.potion == PotionTypes.EMPTY); // PAIL: rename
    }


}
