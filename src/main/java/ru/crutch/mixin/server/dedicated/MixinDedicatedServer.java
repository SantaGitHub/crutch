package ru.crutch.mixin.server.dedicated;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.management.PlayerList;
import ru.crutch.CrutchServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;
import ru.crutch.mixin.server.MixinMinecraftServer;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer extends MixinMinecraftServer {
	@Shadow PropertyManager settings;
	
	@Override
	public PropertyManager getPropertyManager() {
		return settings;
	}
}
