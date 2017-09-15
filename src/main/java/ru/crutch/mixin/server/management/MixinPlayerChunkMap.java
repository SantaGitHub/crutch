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
public abstract class MixinPlayerChunkMap implements IMixinPlayerChunkMap {

    public boolean isChunkInUse(int x, int z) {
        PlayerChunkMapEntry pi = getEntry(x, z);
        if (pi != null) {
            return ((IMixinPlayerChunkMapEntry) pi).getPlayers().size() > 0;
        }
        return false;
    }

	@Override
	@Shadow
	public abstract PlayerChunkMapEntry getEntry(int x, int z);

}
