package ru.crutch.interfaces.entity.item;

import ru.crutch.interfaces.entity.IMixinEntity;

public interface IMixinEntityItem extends IMixinEntity {
    void setdelayBeforeCanPickup(int i);
    int getdelayBeforeCanPickup();
}
