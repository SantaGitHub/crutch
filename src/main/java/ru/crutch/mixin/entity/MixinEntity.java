package ru.crutch.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.world.IMixinWorld;
import ru.crutch.interfaces.entity.IMixinEntity;

@Mixin(net.minecraft.entity.Entity.class)
public abstract class MixinEntity implements IMixinEntity{
    @Shadow private int fire;
    @Shadow public World world;
    @Shadow public float rotationYaw;
    @Shadow public float rotationPitch;
    @Shadow public Entity ridingEntity;
    @Shadow protected boolean isImmuneToFire;
    @Shadow public int hurtResistantTime;
    @Shadow public double posX;
    @Shadow public double posY;
    @Shadow public double posZ;

    @Shadow public abstract void setLocationAndAngles(double x, double y, double z, float yaw, float pitch);
    @Shadow public abstract void setFire(int p_70015_1_);
    @Shadow public abstract boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_);
    @Shadow public abstract int getAir();
    @Shadow public abstract void setAir(int p_70050_1_);
    @Shadow protected abstract void setBeenAttacked();


    protected CraftEntity bukkitEntity;
    public ProjectileSource projectileSource;
    public String spawnReason;

    @Override
    public int getFireTicks()
    {
        return fire;
    }

    @Override
    public void setFireTicks(int ticks)
    {
        this.fire = ticks;
    }

    @Override
    public CraftEntity getBukkitEntity()
    {
        if(bukkitEntity == null)
            bukkitEntity = CraftEntity.getEntity(((IMixinWorld) world).getServer(), (Entity) (Object) this);
        return bukkitEntity;
    }

    @Override
    public ProjectileSource getProjectileSource()
    {
        return projectileSource;
    }

    @Override
    public String getSpawnReason()
    {
        return spawnReason;
    }

    @Override
    public void setSpawnReason(String spawnReason)
    {
        this.spawnReason = spawnReason;
    }

    @Override
    public void setProjectileSource(ProjectileSource projectileSource)
    {
        this.projectileSource = projectileSource;
    }

    @Override
    public void teleportTo(Location exit, boolean portal)
    {
        throw new UnsupportedOperationException(); //TODO
    }



}
