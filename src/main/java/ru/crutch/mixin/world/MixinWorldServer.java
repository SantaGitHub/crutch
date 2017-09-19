package ru.crutch.mixin.world;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.player.IMixinEntityPlayerMP;
import ru.crutch.interfaces.world.IMixinWorld;
import ru.crutch.interfaces.world.IMixinWorldServer;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements IMixinWorldServer {



    @Shadow
    public abstract void sendPacketWithinDistance(EntityPlayerMP player, boolean longDistance, double x, double y, double z, Packet<?> packetIn);

    @Override
    public void sendParticles(EntityPlayerMP sender, EnumParticleTypes enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        final SPacketParticles packetplayoutworldparticles = new SPacketParticles(enumparticle, flag, (float)d0, (float)d1, (float)d2, (float)d3, (float)d4, (float)d5, (float)d6, i, aint);
        for (int j = 0; j < ((World)((IMixinWorld) this)).playerEntities.size(); ++j) {
            final EntityPlayerMP entityplayer = (EntityPlayerMP) ((World)((IMixinWorld) this)).playerEntities.get(j);
            if (sender == null || ((IMixinEntityPlayerMP) entityplayer).getBukkitEntity().canSee(((IMixinEntityPlayerMP) sender).getBukkitEntity())) {
                //final BlockPos blockposition = entityplayer.getPosition();
                //blockposition.distanceSq(d0, d1, d2);
                this.sendPacketWithinDistance(entityplayer, flag, d0, d1, d2, packetplayoutworldparticles);
            }
        }
    }
}
