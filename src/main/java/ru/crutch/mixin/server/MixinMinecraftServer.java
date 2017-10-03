package ru.crutch.mixin.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import jline.console.ConsoleReader;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.management.PlayerList;

import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.io.IOException;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IMixinMinecraftServer {
	@Shadow public static final Logger LOG = LogManager.getLogger();
	@Shadow	private final YggdrasilAuthenticationService authService;
	@Shadow	private final MinecraftSessionService sessionService;
	@Shadow	private final GameProfileRepository profileRepo;
	@Shadow	private final PlayerProfileCache profileCache;
	@Shadow	private final NetworkSystem networkSystem;
	@Shadow	private final ICommandManager commandManager;
	@Shadow private final Thread serverThread;
	@Shadow private final DataFixer dataFixer;
	@Shadow public WorldServer[] worlds;
	@Shadow PlayerList playerList;
	@Shadow public Proxy serverProxy;
	public CraftServer server;
	OptionSet options;
	public int autosavePeriod;

	public final int currentTick = (int) (System.currentTimeMillis() / 50);
	public final Thread primaryThread;
	public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();

	public org.bukkit.command.ConsoleCommandSender console;
	public ConsoleReader reader;

	public MixinMinecraftServer(OptionSet options, Proxy proxyIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn){
		this.serverProxy = proxyIn;
		this.authService = authServiceIn;
		this.sessionService = sessionServiceIn;
		this.profileRepo = profileRepoIn;
		this.profileCache = profileCacheIn;
		this.networkSystem = new NetworkSystem( ((MinecraftServer)(IMixinMinecraftServer)this));
		this.commandManager = ((MinecraftServer)(IMixinMinecraftServer)this).createCommandManager();
		this.dataFixer = dataFixerIn;
		this.options = options;
		if (System.console() == null && System.getProperty("jline.terminal") == null) {
			System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
			Main.useJline = false;
		}

		try {
			reader = new ConsoleReader(System.in, System.out);
			reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
		} catch (Throwable e) {
			try {
				// Try again with jline disabled for Windows users without C++ 2008 Redistributable
				System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
				System.setProperty("user.language", "en");
				Main.useJline = false;
				reader = new ConsoleReader(System.in, System.out);
				reader.setExpandEvents(false);
			} catch (IOException ex) {
				LOG.warn((String) null, ex);
			}
		}
		Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread((MinecraftServer)(IMixinMinecraftServer)this));
		this.serverThread = primaryThread = new Thread((MinecraftServer)(IMixinMinecraftServer)this, "Server thread"); // Moved from main
	}

	@Override
	public Thread getPrimaryThread(){
		return this.primaryThread;
	}

	@Override
	public java.util.Queue<Runnable> getProcessQueue(){
		return this.processQueue;
	}
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
