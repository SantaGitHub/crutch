package ru.crutch.interfaces.entity;

import org.bukkit.craftbukkit.attribute.CraftAttributeMap;

public interface IMixinEntityLivingBase extends IMixinEntity{

    boolean getCollides();
    void setCollides(boolean flag);
    CraftAttributeMap getCraftAttributes();
    int getMaxAirTicks();
    void setMaxAirTicks(int i);
}
