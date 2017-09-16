package ru.crutch.mixin.server.management;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import ru.crutch.interfaces.server.management.IMixinPlayerList;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
	
	CraftServer cserver;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	void onConstruct(MinecraftServer server, CallbackInfo ci) {
		cserver = new CraftServer(server, (PlayerList)((IMixinPlayerList)this));
		cserver.loadPlugins();
        cserver.enablePlugins(PluginLoadOrder.STARTUP);
	}
}
