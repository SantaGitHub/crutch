package ru.crutch.interfaces.world;

public interface IMixinExplosion {

    int floor_double(double value);

    boolean getWasCanceled();

    void doExplosionA();

    void doExplosionB(boolean spawnParticles);
}
