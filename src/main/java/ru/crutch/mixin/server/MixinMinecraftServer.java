package ru.crutch.mixin.server;

import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;

import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IMixinMinecraftServer {
	CraftServer server;
	OptionSet options;
	
	@Override
	public CraftServer getServer() {
		return server;
	}
	
	@Override
	public OptionSet getOptionSet() {
		return options;
	}
	
}
