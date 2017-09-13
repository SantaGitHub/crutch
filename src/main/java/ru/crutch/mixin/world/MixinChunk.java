package ru.crutch.mixin.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.crutch.interfaces.world.IMixinChunk;
import ru.crutch.interfaces.world.IMixinWorld;

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

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstructed(World world, int cx, int cz, CallbackInfo ci)
    {
        if(!((Object) this instanceof EmptyChunk))
            bukkitChunk = new CraftChunk((Chunk) (Object) this);
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
