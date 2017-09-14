package ru.crutch.mixin.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.IMixinEntity;
import ru.crutch.interfaces.world.IMixinExplosion;
import ru.crutch.interfaces.world.IMixinWorld;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Mixin(net.minecraft.world.Explosion.class)
public class MixinExplosion implements IMixinExplosion {

    @Shadow public final float explosionSize;
    @Shadow public final double explosionX;
    @Shadow public final double explosionY;
    @Shadow public final double explosionZ;
    @Shadow public final Entity exploder;
    @Shadow private final boolean isFlaming;
    @Shadow private final boolean isSmoking;
    @Shadow private final Random explosionRNG;
    @Shadow private final List<BlockPos> affectedBlockPositions;
    @Shadow private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
    @Shadow private final Vec3d position;

    public boolean wasCanceled = false;
    public final World worldObj;

    public MixinExplosion(float explosionSize, double explosionX, double explosionY, double explosionZ, Random explosionRNG, Map<EntityPlayer, Vec3d> playerKnockbackMap, World worldObj, Entity exploder, boolean isFlaming, boolean isSmoking, List<BlockPos> affectedBlockPositions, Vec3d position) {
        this.explosionSize = explosionSize;
        this.explosionX = explosionX;
        this.explosionY = explosionY;
        this.explosionZ = explosionZ;
        this.explosionRNG = explosionRNG;
        this.playerKnockbackMap = playerKnockbackMap;
        this.worldObj = worldObj;
        this.exploder = exploder;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
        this.affectedBlockPositions = affectedBlockPositions;
        this.position = position;
    }

    public boolean getWasCanceled(){
        return this.wasCanceled;
    }

    public int floor_double(double value)
    {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }


    @Override
    public void doExplosionA()  {
        Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                for (int l = 0; l < 16; ++l)
                {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
                    {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        double d4 = this.explosionX;
                        double d6 = this.explosionY;
                        double d8 = this.explosionZ;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
                        {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

                            if (iblockstate.getMaterial() != Material.AIR)
                            {
                                float f2 = this.exploder != null ? this.exploder.getExplosionResistance((Explosion)(IMixinExplosion)this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, (Entity)null, (Explosion)(IMixinExplosion)this);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion((Explosion)(IMixinExplosion)this, this.worldObj, blockpos, iblockstate, f)))
                            {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(set);
        float f3 = this.explosionSize * 2.0F;
        int k1 = floor_double(this.explosionX - (double)f3 - 1.0D);
        int l1 = floor_double(this.explosionX + (double)f3 + 1.0D);
        int i2 = floor_double(this.explosionY - (double)f3 - 1.0D);
        int i1 = floor_double(this.explosionY + (double)f3 + 1.0D);
        int j2 = floor_double(this.explosionZ - (double)f3 - 1.0D);
        int j1 = floor_double(this.explosionZ + (double)f3 + 1.0D);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, (Explosion)(IMixinExplosion)this, list, f3);
        Vec3d vec3d = new Vec3d(this.explosionX, this.explosionY, this.explosionZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);

            if (!entity.isImmuneToExplosions())
            {
                double d12 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)f3;

                if (d12 <= 1.0D)
                {
                    double d5 = entity.posX - this.explosionX;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - this.explosionY;
                    double d9 = entity.posZ - this.explosionZ;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                    if (d13 != 0.0D)
                    {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)this.worldObj.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double d10 = (1.0D - d12) * d14;
                        //entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                        // CraftBukkit start
                        CraftEventFactory.entityDamage = exploder;
                        ((IMixinEntity) entity).setForceExplosionKnockback(false);
                        boolean wasDamaged = entity.attackEntityFrom(DamageSource.causeExplosionDamage((Explosion)(IMixinExplosion) this), (float) ((int) ((d13 * d13 + d13) / 2.0D * 7.0D * (double) f3 + 1.0D)));
                        CraftEventFactory.entityDamage = null;
                        if (!wasDamaged && !(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) && !((IMixinEntity) entity).getForceExplosionKnockback()) {
                            continue;
                        }
                        // CraftBukkit end
                        double d11 = 1.0D;

                        if (entity instanceof EntityLivingBase)
                        {
                            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                        }

                        entity.motionX += d5 * d11;
                        entity.motionY += d7 * d11;
                        entity.motionZ += d9 * d11;

                        if (entity instanceof EntityPlayer)
                        {
                            EntityPlayer entityplayer = (EntityPlayer)entity;

                            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying))
                            {
                                this.playerKnockbackMap.put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        this.worldObj.playSound((EntityPlayer)null, this.explosionX, this.explosionY, this.explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.explosionSize >= 2.0F && this.isSmoking)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }
        else
        {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }

        if (this.isSmoking)
        {
            // CraftBukkit start
            final org.bukkit.World bworld = ((IMixinWorld) this.worldObj).getWorld();
            final org.bukkit.entity.Entity explode = (this.exploder == null) ? null : ((IMixinEntity) this.exploder).getBukkitEntity();
            final Location location = new Location(bworld, this.explosionX, this.explosionY, this.explosionZ);
            final List<org.bukkit.block.Block> blockList = /*(List<Block>)*/Lists.newArrayList();
            for (int i1 = this.affectedBlockPositions.size() - 1; i1 >= 0; --i1) {
                final BlockPos cpos = this.affectedBlockPositions.get(i1);
                final org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
                if (bblock.getType() != org.bukkit.Material.AIR) {
                    blockList.add(bblock);
                }
            }
            boolean cancelled;
            List<org.bukkit.block.Block> bukkitBlocks;
            float yield;
            if (explode != null) {
                final EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, 1.0f / this.explosionSize);
                ((IMixinWorld) this.worldObj).getServer().getPluginManager().callEvent(event);
                cancelled = event.isCancelled();
                bukkitBlocks = event.blockList();
                yield = event.getYield();
            }
            else {
                final BlockExplodeEvent event2 = new BlockExplodeEvent(location.getBlock(), blockList, 1.0f / this.explosionSize);
                ((IMixinWorld) this.worldObj).getServer().getPluginManager().callEvent(event2);
                cancelled = event2.isCancelled();
                bukkitBlocks = event2.blockList();
                yield = event2.getYield();
            }
            this.affectedBlockPositions.clear();
            for (final org.bukkit.block.Block bblock2 : bukkitBlocks) {
                final BlockPos coords = new BlockPos(bblock2.getX(), bblock2.getY(), bblock2.getZ());
                this.affectedBlockPositions.add(coords);
            }
            if (cancelled) {
                this.wasCanceled = true;
                return;
            }
            // CraftBukkit end
            for (BlockPos blockpos : this.affectedBlockPositions)
            {
                IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (spawnParticles)
                {
                    double d0 = (double)((float)blockpos.getX() + this.worldObj.rand.nextFloat());
                    double d1 = (double)((float)blockpos.getY() + this.worldObj.rand.nextFloat());
                    double d2 = (double)((float)blockpos.getZ() + this.worldObj.rand.nextFloat());
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = (double)Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 = d3 / d6;
                    d4 = d4 / d6;
                    d5 = d5 / d6;
                    double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
                    d7 = d7 * (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                    d3 = d3 * d7;
                    d4 = d4 * d7;
                    d5 = d5 * d7;
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5, new int[0]);
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                }

                if (iblockstate.getMaterial() != Material.AIR)
                {
                    if (block.canDropFromExplosion((Explosion)(IMixinExplosion)this))
                    {
                        block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0F / this.explosionSize, 0);
                    }

                    block.onBlockExploded(this.worldObj, blockpos, (Explosion)(IMixinExplosion)this);
                }
            }
        }

        if (this.isFlaming)
        {
            for (BlockPos blockpos1 : this.affectedBlockPositions)
            {
                if (this.worldObj.getBlockState(blockpos1).getMaterial() == Material.AIR && this.worldObj.getBlockState(blockpos1.down()).isFullBlock() && this.explosionRNG.nextInt(3) == 0)
                {
                    this.worldObj.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }
}
