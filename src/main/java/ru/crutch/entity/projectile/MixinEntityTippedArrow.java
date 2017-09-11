package ru.crutch.entity.projectile;

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

import java.util.Set;

@Mixin(EntityTippedArrow.class)
public abstract class MixinEntityTippedArrow extends EntityArrow {

    @Shadow
    private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityTippedArrow.class, DataSerializers.VARINT);

    private PotionType potion;
    private Set<PotionEffect> customPotionEffects = Sets.<PotionEffect>newHashSet();

    public MixinEntityTippedArrow(World worldIn) {
        super(worldIn);
    }

    public void refreshEffects() {
        this.dataManager.set(this.COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    public String getType() {
        return (PotionType.REGISTRY.getNameForObject(this.potion)).toString();
    }

    public void setType(String string) {
        this.potion = PotionType.REGISTRY.getObject(new ResourceLocation(string));
        this.dataManager.set(this.COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    public boolean isTipped() {
        return !(this.customPotionEffects.isEmpty() && this.potion == PotionTypes.EMPTY); // PAIL: rename
    }

}
