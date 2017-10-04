package ru.crutch.interfaces.tileentity;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public interface IMixinTileEntityBeacon {
    void onOpen(CraftHumanEntity who);

    void onClose(CraftHumanEntity who);

    List<HumanEntity> getViewers();

    void setMaxStackSize(int size);

    org.bukkit.potion.PotionEffect getPrimaryEffect();

    org.bukkit.potion.PotionEffect getSecondaryEffect();

    byte getAmplification();

    boolean hasSecondaryEffect();

    List getHumansInRange();

    int getLevel();
}
