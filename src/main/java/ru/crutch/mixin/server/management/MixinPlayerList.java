package ru.crutch.mixin.server.management;

import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import ru.crutch.interfaces.server.IMixinMinecraftServer;
import ru.crutch.interfaces.server.management.IMixinPlayerList;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements IMixinPlayerList {

	@Shadow @Final
	public final List<EntityPlayerMP> playerEntityList = new java.util.concurrent.CopyOnWriteArrayList<EntityPlayerMP>();

	@Override
	public List<EntityPlayerMP> getPlayerEntityList(){
		return this.playerEntityList;
	}

	CraftServer cserver;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	void onConstruct(MinecraftServer server, CallbackInfo ci) {
		cserver = new CraftServer(server, (PlayerList)((IMixinPlayerList)this));
		cserver.loadPlugins();
        cserver.enablePlugins(PluginLoadOrder.STARTUP);
	}
}
