package ru.crutch.interfaces.server;

import jline.console.ConsoleReader;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.management.PlayerList;

import org.bukkit.craftbukkit.CraftServer;

import joptsimple.OptionSet;

public interface IMixinMinecraftServer {
	Thread getPrimaryThread();

	java.util.Queue<Runnable> getProcessQueue();

    org.bukkit.command.ConsoleCommandSender getConsole();
	CraftServer getServer();
	OptionSet getOptionSet();
	PropertyManager getPropertyManager();
	void setAutosavePeriod(int per);
    int getAutosavePeriod();
	ConsoleReader getReader();

}
