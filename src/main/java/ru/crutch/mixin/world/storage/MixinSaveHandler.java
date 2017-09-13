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

    private UUID uuid = null; // CraftBukkit
    private static boolean initializedBukkit = false;

    @Inject(method = "loadWorldInfo", cancellable = true, at = @At("RETURN"))
    public void onLoadWorldInfo(CallbackInfoReturnable<WorldInfo> ci)
    {
        initBukkitData();
    }

    private void initBukkitData()
    {
        // inject bukkit materials before plugins load
        if(!initializedBukkit)
        {
            //injectBlockBukkitMaterials(); //TODO
            //injectItemBukkitMaterials();
            // since we modify bukkit enums, we need to guarantee that plugins are
            // loaded after all mods have been loaded by FML to avoid race conditions.
            ((CraftServer) Bukkit.getServer()).loadPlugins();
            ((CraftServer) Bukkit.getServer()).enablePlugins(org.bukkit.plugin.PluginLoadOrder.STARTUP);
            initializedBukkit = true;
        }
    }
    /*
    @SuppressWarnings("deprecation")
    private static void injectItemBukkitMaterials()
    {
        FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();
        List<Integer> ids = new ArrayList<Integer>();

        for(Item thing : itemRegistry.typeSafeIterable())
        {
            ids.add(itemRegistry.getId(thing));
        }

        // sort by id
        Collections.sort(ids);

        for(int id : ids)
        {
            Item item = itemRegistry.getRaw(id);
            // inject item materials into Bukkit for FML
            org.bukkit.Material material = org.bukkit.Material.addMaterial(id, itemRegistry.getNameForObject(item), false);
            if(material != null)
            {
                FMLLog.fine("Injected new Forge item material %s with ID %d.", material.name(), material.getId());
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void injectBlockBukkitMaterials()
    {
        FMLControlledNamespacedRegistry<Block> blockRegistry = GameData.getBlockRegistry();
        List<Integer> ids = new ArrayList<Integer>();

        for(Block block : blockRegistry.typeSafeIterable())
        {
            ids.add(blockRegistry.getId(block));
        }

        // sort by id
        Collections.sort(ids);

        for(int id : ids)
        {
            Block block = blockRegistry.getRaw(id);
            // inject block materials into Bukkit for FML
            org.bukkit.Material material = org.bukkit.Material.addMaterial(id, blockRegistry.getNameForObject(block), true);
            if(material != null)
            {
                FMLLog.fine("Injected new Forge block material %s with ID %d.", material.name(), material.getId());
            }
        }
    }*/


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
