package ru.crutch.mixin.world.storage;

import net.minecraft.world.storage.MapData;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.world.storage.IMixinMapData;


@Mixin(MapData.class)
public abstract class MixinMapData implements IMixinMapData {
    public final CraftMapView mapView = new CraftMapView((MapData) (IMixinMapData)this);
    @Shadow public boolean trackingPosition;
    @Shadow public byte scale;


    @Override
    public CraftMapView getMapView(){
        return this.mapView;
    }

    @Override
    public byte getScale(){
        return this.scale;
    }
    @Override
    public boolean getTrackingPosition(){
        return this.trackingPosition;
    }

}
