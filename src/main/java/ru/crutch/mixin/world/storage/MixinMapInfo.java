package ru.crutch.mixin.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.Vec4b;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.crutch.interfaces.entity.IMixinEntity;
import ru.crutch.interfaces.world.storage.IMixinMapData;


@Mixin(MapData.MapInfo.class)
public abstract class MixinMapInfo {

    @Shadow
    public EntityPlayer entityplayerObj;
    @Shadow private int minX;
    @Shadow private int minY;
    @Shadow private int maxX = 127;
    @Shadow private int maxY = 127;
    @Shadow
    public boolean isDirty;
    @Shadow int tick;

    @Redirect(method = "getPacket", at=@At("INVOKE"))
    public Packet<?> getPacket(ItemStack stack) {
        org.bukkit.craftbukkit.map.RenderData render = ((IMixinMapData) this).getMapView().render((org.bukkit.craftbukkit.entity.CraftPlayer) ((IMixinEntity) this.entityplayerObj).getBukkitEntity());
        java.util.Collection<Vec4b> icons = new java.util.ArrayList<Vec4b>();
        for ( org.bukkit.map.MapCursor cursor : render.cursors) {
            if (cursor.isVisible()) {
                icons.add(new Vec4b(cursor.getRawType(), cursor.getX(), cursor.getY(), cursor.getDirection()));
            }
        }
        if (this.isDirty)
        {
            this.isDirty = false;
            return new SPacketMaps(stack.getMetadata(), ((IMixinMapData) this).getScale(), ((IMixinMapData) this).getTrackingPosition(), icons, render.buffer, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
        }
        else
        {
            return (this.tick++ % 5 == 0) ? new SPacketMaps(stack.getMetadata(), ((IMixinMapData) this).getScale(), ((IMixinMapData) this).getTrackingPosition(), icons, render.buffer, 0, 0, 0, 0) : null;
        }


    }

}
