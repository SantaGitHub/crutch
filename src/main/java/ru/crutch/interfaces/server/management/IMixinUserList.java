package ru.crutch.interfaces.server.management;

import net.minecraft.server.management.UserListEntry;

import java.util.Collection;
import java.util.Map;

public interface IMixinUserList<K, V extends UserListEntry<K>> {
    Collection<V> getValuesCB();
}
