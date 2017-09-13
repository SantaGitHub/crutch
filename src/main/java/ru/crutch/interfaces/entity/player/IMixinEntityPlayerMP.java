package ru.crutch.interfaces.entity.player;

import org.bukkit.WeatherType;

public interface IMixinEntityPlayerMP {

    long getPlayerTime();

    WeatherType getPlayerWeather();

    void setPlayerWeather(WeatherType type, boolean plugin);

    void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder);

    void tickWeather();

    void resetPlayerWeather();


}
