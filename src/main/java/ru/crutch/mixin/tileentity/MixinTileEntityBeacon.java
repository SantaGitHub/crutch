package ru.crutch.mixin.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.tileentity.IMixinTileEntityBeacon;

import javax.annotation.Nullable;
import java.util.List;

import static ru.crutch.inventory.ICBInventory.MAX_STACK;

@Mixin(TileEntityBeacon.class)
public class MixinTileEntityBeacon extends TileEntity implements IMixinTileEntityBeacon {
    @Shadow private int levels = -1;
    @Shadow private ItemStack payment;
    @Nullable @Shadow
    private Potion primaryEffect;
    @Nullable @Shadow
    private Potion secondaryEffect;

    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return new ItemStack[] { this.payment };
    }
    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }
    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }
    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }
    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    @Override
    public org.bukkit.potion.PotionEffect getPrimaryEffect() {
        return (this.primaryEffect != null) ? CraftPotionUtil.toBukkit(new net.minecraft.potion.PotionEffect(this.primaryEffect, this.getLevel(), this.getAmplification(), true, true)) : null;
    }
    @Override
    public org.bukkit.potion.PotionEffect getSecondaryEffect() {
        return (hasSecondaryEffect()) ? CraftPotionUtil.toBukkit(new net.minecraft.potion.PotionEffect(this.secondaryEffect, this.getLevel(), this.getAmplification(), true, true)) : null;
    }

    @Override
    public byte getAmplification() {
        int i = 0;
        if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect)
        {
            i = 1;
        }
        return (byte) i;
    }

    @Override
    public boolean hasSecondaryEffect() {
        if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null)
        {
            return true;
        }
        return false;
    }

    @Override
    public List getHumansInRange() {
       	{
       	    double d0 = (double)(this.levels * 10 + 10);
            int k = this.pos.getX();
            int l = this.pos.getY();
            int i1 = this.pos.getZ();
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).expandXyz(d0).addCoord(0.0D, (double)this.world.getHeight(), 0.0D);
            List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            return list;
        }
    }
    @Override
    public int getLevel() {
        {
        return (9 + this.levels * 2) * 20;
        }
    }
}
