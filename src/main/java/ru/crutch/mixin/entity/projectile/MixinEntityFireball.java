package ru.crutch.mixin.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.projectile.IMixinEntityFireball;

@Mixin(net.minecraft.entity.projectile.EntityFireball.class)
public abstract class MixinEntityFireball extends Entity implements IMixinEntityFireball {

    @Shadow public double accelerationX;
    @Shadow public double accelerationY;
    @Shadow public double accelerationZ;

    public MixinEntityFireball(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setDirection(double accelX, double accelY, double accelZ) {
        accelX = accelX + this.rand.nextGaussian() * 0.4D;
        accelY = accelY + this.rand.nextGaussian() * 0.4D;
        accelZ = accelZ + this.rand.nextGaussian() * 0.4D;
        double d0 = (double)Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.1D;
        this.accelerationY = accelY / d0 * 0.1D;
        this.accelerationZ = accelZ / d0 * 0.1D;
    }
}
