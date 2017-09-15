package ru.crutch.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import ru.crutch.interfaces.world.IMixinWorld;
import ru.crutch.interfaces.entity.IMixinEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {
	@Shadow
	private int fire;
	@Shadow
	public World world;
	@Shadow
	public float rotationYaw;
	@Shadow
	public float rotationPitch;
	@Shadow
	public Entity ridingEntity;
	@Shadow
	protected boolean isImmuneToFire;
	@Shadow
	public int hurtResistantTime;
	@Shadow
	public double posX;
	@Shadow
	public double posY;
	@Shadow
	public double posZ;

	@Shadow
	public EntityDataManager dataManager;

	public boolean forceExplosionKnockback = false;
	protected CraftEntity bukkitEntity;
	public ProjectileSource projectileSource;
	public String spawnReason;

	public boolean getForceExplosionKnockback() {
		return this.forceExplosionKnockback;
	}

	public void setForceExplosionKnockback(boolean flag) {
		this.forceExplosionKnockback = flag;
	}

	@Override
	public int getFireTicks() {
		return fire;
	}

	@Override
	public void setFireTicks(int ticks) {
		this.fire = ticks;
	}

	@Override
	public CraftEntity getBukkitEntity() {
		if (bukkitEntity == null)
			bukkitEntity = CraftEntity.getEntity(((IMixinWorld) world).getServer(), (Entity) (Object) this);
		return bukkitEntity;
	}

	@Override
	public ProjectileSource getProjectileSource() {
		return projectileSource;
	}

	@Override
	public String getSpawnReason() {
		return spawnReason;
	}

	@Override
	public void setSpawnReason(String spawnReason) {
		this.spawnReason = spawnReason;
	}

	@Override
	public void setProjectileSource(ProjectileSource projectileSource) {
		this.projectileSource = projectileSource;
	}

	@Override
	public void setPassengerOf(Entity entity) {

	}

	@Override
	public void teleportTo(Location exit, boolean portal) {
		throw new UnsupportedOperationException(); // TODO
	}
	
	@Override
	@Shadow
	public abstract EntityDataManager getDataManager();

}
