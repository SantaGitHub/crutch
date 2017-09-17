package ru.crutch.mixin.world.storage;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.crutch.interfaces.world.storage.IMixinSaveHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Mixin(net.minecraft.world.storage.SaveHandler.class)
public abstract class MixinSaveHandler implements IMixinSaveHandler {


    private static Logger logger;
    @Shadow private File worldDirectory;

    @Shadow public abstract WorldInfo loadWorldInfo();

    private UUID uuid = null;

    @Override
    public UUID getUUID() {
        if(uuid != null)
        {
            return uuid;
        }

        File file1 = new File(this.worldDirectory, "uid.dat");

        if(file1.exists())
        {
            DataInputStream dis = null;

            try
            {
                dis = new DataInputStream(new FileInputStream(file1));
                return uuid = new UUID(dis.readLong(), dis.readLong());
            } catch(IOException ex)
            {
                logger.warn("Failed to read " + file1 + ", generating new random UUID", ex);
            } finally
            {
                if(dis != null)
                {
                    try
                    {
                        dis.close();
                    } catch(IOException ex)
                    {
                        // NOOP
                    }
                }
            }
        }

        uuid = UUID.randomUUID();

        DataOutputStream dos = null;
        try
        {
            dos = new DataOutputStream(new FileOutputStream(file1));
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
        } catch(IOException ex)
        {
            logger.warn("Failed to write " + file1, ex);
        } finally
        {
            if(dos != null)
            {
                try
                {
                    dos.close();
                } catch(IOException ex)
                {
                    // NOOP
                }
            }
        }

        return uuid;

    }
}
