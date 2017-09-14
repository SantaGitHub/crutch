package ru.crutch.interfaces.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.projectiles.ProjectileSource;

public interface IMixinEntity {

    EntityDataManager getDataManager();

    void setForceExplosionKnockback(boolean flag);

    boolean getForceExplosionKnockback();

    int getFireTicks();

    void setFireTicks(int ticks);

    CraftEntity getBukkitEntity();

    ProjectileSource getProjectileSource();

    void setProjectileSource(ProjectileSource projectileSource);

    void setPassengerOf(Entity entity);

    void teleportTo(Location exit, boolean portal);

    String getSpawnReason();

    void setSpawnReason(String spawnReason);

}
