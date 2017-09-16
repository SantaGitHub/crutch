package ru.crutch.interfaces.server.management;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public interface IMixinPlayerList {
    List<EntityPlayerMP> getPlayerEntityList();
}
