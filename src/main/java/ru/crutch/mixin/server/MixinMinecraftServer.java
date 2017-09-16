package ru.crutch.mixin.server;

import net.minecraft.server.dedicated.PropertyManager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.world.WorldLoadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;
import ru.crutch.interfaces.world.IMixinWorld;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IMixinMinecraftServer {
	@Shadow public WorldServer[] worlds;
	CraftServer server;
	OptionSet options;
	public int autosavePeriod;

	@Override
	public int getAutosavePeriod(){
		return this.autosavePeriod;
	}
	@Override
	public void setAutosavePeriod(int per){
		this.autosavePeriod = per;
	}

	@Override
	public CraftServer getServer() {
		return server;
	}

	@Override
	public abstract PropertyManager getPropertyManager();

	@Override
	public OptionSet getOptionSet() {
		return options;
	}
	
	@Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
	public void onWorldLoadComplete(CallbackInfo ci) {
		for (WorldServer world : this.worlds) {
            this.server.getPluginManager().callEvent(new WorldLoadEvent(((IMixinWorld)world).getWorld()));
        }
		
		this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD); // CraftBukkit
	}
	
}
