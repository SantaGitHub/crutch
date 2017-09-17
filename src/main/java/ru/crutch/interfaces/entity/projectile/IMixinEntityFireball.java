package ru.crutch.interfaces.entity.projectile;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityFireball extends IMixinEntity {


    void setDirection(double accelX, double accelY, double accelZ);
}
