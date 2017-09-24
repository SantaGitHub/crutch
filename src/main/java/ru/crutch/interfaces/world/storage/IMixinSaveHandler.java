package ru.crutch.interfaces.world.storage;

import java.io.File;
import java.util.UUID;

public interface IMixinSaveHandler {

    UUID getUUID();
    File getPlayerDir();

}
