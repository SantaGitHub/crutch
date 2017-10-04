package ru.crutch.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import ru.crutch.interfaces.entity.IMixinEntityLivingBase;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IMixinEntityLivingBase{

    private int expToDrop;
    public CraftAttributeMap craftAttributes;
    public int maxAirTicks = 300;
    public boolean collides = true;

    @Override
    public boolean getCollides(){
        return this.collides;
    }
    @Override
    public void setCollides(boolean flag){
        this.collides = flag;
    }

    @Override
    public CraftAttributeMap getCraftAttributes(){
        return this.craftAttributes;
    }

    @Override
    public void setMaxAirTicks(int i){
        this.maxAirTicks = i;
    }

    @Override
    public int getMaxAirTicks(){
        return this.maxAirTicks;
    }


    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Override
    public int getExpToDrop() {
        return expToDrop;
    }
    @Override
    public void setExpToDrop(int expToDrop) {
        this.expToDrop = expToDrop;
    }
}
