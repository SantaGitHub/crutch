package ru.crutch.interfaces.entity.player;

import net.minecraft.util.text.ITextComponent;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityPlayerMP extends IMixinEntity {

    int nextContainerCounter();

    String getdisplayName();
    void setdisplayName(String name);
    ITextComponent getListName();
    void setListName(ITextComponent listName);
    Location getCompassTarget();
    void setCompassTarget(Location loc);

    long getPlayerTime();

    WeatherType getPlayerWeather();

    void setPlayerWeather(WeatherType type, boolean plugin);

    void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder);

    void tickWeather();

    void resetPlayerWeather();
    
    @Override
    CraftPlayer getBukkitEntity();


}
