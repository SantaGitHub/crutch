package ru.crutch.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class MixinWorld{

    private CraftWorld world;
    public boolean pvpMode;
    public ChunkGenerator generator;

    /*
    public ArrayList<BlockState> capturedBlockStates = new ArrayList<BlockState>(){
        @Override
        public boolean add(BlockState blockState ) {
            Iterator<BlockState> blockStateIterator = this.iterator();
            while( blockStateIterator.hasNext() ) {
                BlockState blockState1 = blockStateIterator.next();
                if ( blockState1.getLocation().equals( blockState.getLocation() ) ) {
                    return false;
                }
            }

            return super.add( blockState );
        }
    };*/
    public long ticksPerAnimalSpawns;
    public long ticksPerMonsterSpawns;
    public boolean populating;
    private int tickPosition;
    @Shadow
    private IChunkProvider chunkProvider;
    
    public CraftWorld getWorld() {
        return this.world;
    }

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public Chunk getChunkIfLoaded(int x, int z) {
        return ((net.minecraft.world.gen.ChunkProviderServer) this.chunkProvider).getLoadedChunk(x, z);
    }

}
