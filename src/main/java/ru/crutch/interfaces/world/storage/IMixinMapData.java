package ru.crutch.interfaces.world.storage;

import org.bukkit.craftbukkit.map.CraftMapView;

public interface IMixinMapData {

    byte getScale();
    boolean getTrackingPosition();
    CraftMapView getMapView();

}
