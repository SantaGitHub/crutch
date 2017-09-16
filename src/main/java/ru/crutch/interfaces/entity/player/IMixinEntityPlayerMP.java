package ru.crutch.interfaces.entity.player;

import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityPlayerMP extends IMixinEntity {

    long getPlayerTime();

    WeatherType getPlayerWeather();

    void setPlayerWeather(WeatherType type, boolean plugin);

    void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder);

    void tickWeather();

    void resetPlayerWeather();
    
    @Override
    CraftPlayer getBukkitEntity();


}
