package ru.crutch.mixin.world.gen;

import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.world.gen.IMixinChunkProviderServer;

import java.util.Set;
@Mixin(net.minecraft.world.gen.ChunkProviderServer.class)
public class MixinChunkProviderServer implements IMixinChunkProviderServer {

    @Shadow
    Set<Long> droppedChunksSet;

    @Override
    public Set<Long> getdroppedChunksSet() {
        return droppedChunksSet;
    }
}
