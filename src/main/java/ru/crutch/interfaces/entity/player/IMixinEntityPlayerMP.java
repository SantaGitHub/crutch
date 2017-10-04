package ru.crutch.interfaces.entity.player;

import net.minecraft.util.text.ITextComponent;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityPlayerMP extends IMixinEntity {

    void setNewExp(int i);

    void setNewLevel(int i);

    void setNewTotalExp(int i);

    void setKeepLevel(boolean flag);

    int getNewExp();

    int getNewLevel();

    int getNewTotalExp();

    boolean getKeepLevel();

    void setSpawnWorld(String world);

    String getSpawnWorld();

    boolean getFauxSleeping();

    void setFauxSleeping(boolean flag);

    long getTimeOffset();

    boolean getRelativeTime();

    void setTimeOffset(long time);

    void setRelativeTime(boolean flag);

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


    double getMaxHealthCache();

    void setMaxHealthCache(double maxHealthCache);

    boolean isJoining();

    void setJoining(boolean joining);
}
