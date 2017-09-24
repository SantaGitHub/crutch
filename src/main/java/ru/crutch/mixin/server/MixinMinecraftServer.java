package ru.crutch.mixin.server;

import jline.console.ConsoleReader;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.management.PlayerList;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import ru.crutch.CrutchServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;
import ru.crutch.interfaces.server.management.IMixinPlayerList;
import ru.crutch.interfaces.world.IMixinWorld;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IMixinMinecraftServer {
	@Shadow public WorldServer[] worlds;
	@Shadow PlayerList playerList;
	public CraftServer server;
	OptionSet options;
	public int autosavePeriod;

	public org.bukkit.command.ConsoleCommandSender console;
	public ConsoleReader reader;

	@Override
	public org.bukkit.command.ConsoleCommandSender getConsole(){
		return this.console;
	}
	@Override
	public ConsoleReader getReader(){
		return this.reader;
	}


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
		if(options == null) {
			CrutchServer.debug("generate default OptionSet");
			options = org.bukkit.craftbukkit.Main.main(new String[0]); // Returns default OptionSet. Using this cos we can't change main()
		}
		return options;
	}
	
	@Inject(method = "setPlayerList", at = @At("RETURN"))
	public void onPlayerListConstructed(CallbackInfo ci) {
		server = new CraftServer((MinecraftServer)((IMixinMinecraftServer)this), playerList);
		server = (CraftServer) Bukkit.getServer();
		CrutchServer.debug("load plugins, STARTUP");
		server.loadPlugins();
		server.enablePlugins(PluginLoadOrder.STARTUP);
	}
	
	@Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
	public void onIntialChunkLoad(CallbackInfo ci) { // Calls from server's init method
		CrutchServer.debug("worldLoadEvent");
		
		for (WorldServer world : this.worlds) {
            this.server.getPluginManager().callEvent(new WorldLoadEvent(((IMixinWorld)world).getWorld()));
        }
		
		CrutchServer.debug("enablePlugins POSTWORLD");
		this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD);
	}
	
}
