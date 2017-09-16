package ru.crutch.interfaces.world;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumParticleTypes;

public interface IMixinWorldServer {


    void sendParticles(final EntityPlayerMP sender,
                       final EnumParticleTypes enumparticle,
                       final boolean flag,
                       final double d0,
                       final double d1,
                       final double d2,
                       final int i,
                       final double d3,
                       final double d4,
                       final double d5,
                       final double d6,
                       final int... aint) ;

    void sendPacketWithinDistance(EntityPlayerMP player,
                                  boolean longDistance,
                                  double x, double y, double z,
                                  Packet<?> packetIn);


}
