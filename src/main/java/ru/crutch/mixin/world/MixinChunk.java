package ru.crutch.mixin.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.crutch.interfaces.world.IMixinChunk;
import ru.crutch.interfaces.world.IMixinWorld;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(net.minecraft.world.chunk.Chunk.class)
public abstract class MixinChunk implements IMixinChunk {
    public CraftChunk bukkitChunk;
    private int neighbors = 0x1 << 12;

    @Shadow
    private  World world;
    @Shadow
    private boolean isTerrainPopulated;

    @Shadow
    int xPosition;
    @Shadow
    int zPosition;

    @Mutable @Final @Shadow public Map<BlockPos, TileEntity> chunkTileEntityMap;
    @Mutable @Final @Shadow public ClassInheritanceMultiMap<Entity>[] entityLists;
    @Mutable @Final @Shadow public ExtendedBlockStorage[] storageArrays;
    @Mutable @Final @Shadow public byte[] blockBiomeArray;
    @Mutable @Final @Shadow public int[] precipitationHeightMap;
    @Mutable @Final @Shadow public boolean[] updateSkylightColumns;
    @Mutable @Final @Shadow public int queuedLightChecks;
    @Mutable @Final @Shadow private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;
    @Mutable @Final @Shadow int[] heightMap;

    @Shadow
    public abstract void populateChunk(IChunkGenerator generator);

    @Shadow
    public abstract World getWorld();

    public CraftChunk getBukkitChunk()
    {
        return bukkitChunk;
    }

    public void setBukkitChunk(CraftChunk bukkitChunk)
    {
        this.bukkitChunk = bukkitChunk;
    }

    @Redirect(method = "<init>", at = @At("RETURN"))
    private void onConstructed(World world, int cx, int cz, CallbackInfo ci)
    {
        this.storageArrays = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.precipitationHeightMap = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.chunkTileEntityMap = Maps.<BlockPos, TileEntity>newHashMap();
        this.queuedLightChecks = 4096;
        this.tileEntityPosQueue = Queues.<BlockPos>newConcurrentLinkedQueue();
        this.entityLists = (ClassInheritanceMultiMap[])(new ClassInheritanceMultiMap[16]);
        this.world = world;
        this.xPosition = cx;
        this.zPosition = cz;
        this.heightMap = new int[256];

        for (int i = 0; i < this.entityLists.length; ++i)
        {
            this.entityLists[i] = new ClassInheritanceMultiMap(Entity.class);
        }

        Arrays.fill((int[])this.precipitationHeightMap, (int) - 999);
        Arrays.fill(this.blockBiomeArray, (byte) - 1);

        if(!((Object) this instanceof EmptyChunk))
            bukkitChunk = new CraftChunk((Chunk) (Object) this);
    }

    @Override
    public Map<BlockPos, TileEntity> getChunkTileEntityMap(){
        return this.chunkTileEntityMap;
    }

    @Override
    public void loadNearby(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, boolean newChunk) {
        Server server =((IMixinWorld) this.world).getServer();
        if (server != null) {
    		/*
    		 * If it's a new world, the first few chunks are generated inside
    		 * the World constructor. We can't reliably alter that, so we have
    		 * no way of creating a CraftWorld/CraftServer at that point.
    		 */
            server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(bukkitChunk, newChunk));
        }

        // Update neighbor counts
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Chunk neighbor = getWorld().getChunkFromChunkCoords(xPosition + x, zPosition + z);
                if(neighbor.isLoaded()){
                    if (neighbor != null) {
                        ((IMixinChunk) neighbor).setNeighborLoaded(-x, -z);
                        setNeighborLoaded(x, z);
                    }
                }
            }
        }
        // CraftBukkit end
        Chunk chunk = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition - 1);
        Chunk chunk1 = chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition);
        Chunk chunk2 = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition + 1);
        Chunk chunk3 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition);

        if (chunk1 != null && chunk2 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition + 1) != null)
        {
            this.populateChunk(chunkGenrator);
        }

        if (chunk3 != null && chunk2 != null && chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition + 1) != null)
        {
            ((IMixinChunk) chunk3).populateChunk(chunkGenrator);
        }

        if (chunk != null && chunk1 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition - 1) != null)
        {
            ((IMixinChunk) chunk).populateChunk(chunkGenrator);
        }

        if (chunk != null && chunk3 != null)
        {
            Chunk chunk4 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition - 1);

            if (chunk4 != null)
            {
                ((IMixinChunk) chunk4).populateChunk(chunkGenrator);
            }
        }
    }

    @Override
    public boolean isTerrainPopulated() {
        return this.isTerrainPopulated;
    }

    public void setNeighborLoaded(final int x, final int z) {
        this.neighbors |= 0x1 << (x * 5 + 12 + z);
    }

    public void setNeighborUnloaded(final int x, final int z) {
        this.neighbors &= ~(0x1 << (x * 5 + 12 + z));
    }

    public int getNeighbors() {
        return neighbors;
    }
}
