package ru.crutch.interfaces.entity.projectile;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityTippedArrow extends IMixinEntity {

    void refreshEffects();

    String getType();

    void setType(String string);

    boolean isTipped();

}
