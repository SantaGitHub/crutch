package ru.crutch.mixin.server.management;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.server.management.IMixinPlayerChunkMap;
import ru.crutch.interfaces.server.management.IMixinPlayerChunkMapEntry;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(net.minecraft.server.management.PlayerChunkMap.class)
public class MixinPlayerChunkMap implements IMixinPlayerChunkMap {


    @Shadow
    List<EntityPlayerMP> players;
    @Shadow
    Long2ObjectMap<PlayerChunkMapEntry> entryMap;

    public boolean isChunkInUse(int x, int z) {
        PlayerChunkMapEntry pi = getEntry(x, z);
        if (pi != null) {
            return ((IMixinPlayerChunkMapEntry) pi).getPlayers().size() > 0;
        }
        return false;
    }

    @Nullable
    public PlayerChunkMapEntry getEntry(int x, int z)
    {
        return this.entryMap.get(getIndex(x, z));
    }
    @Override
    public long getIndex(int x, int z)
    {
        return (long)x + 2147483647L | (long)z + 2147483647L << 32;
    }

}
