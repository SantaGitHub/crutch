package ru.crutch.interfaces.entity.projectile;

public interface IMixinEntityTippedArrow {

    void refreshEffects();

    String getType();

    void setType(String string);

    boolean isTipped();

}
