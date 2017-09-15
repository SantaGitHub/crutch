package ru.crutch.interfaces.server;

import org.bukkit.craftbukkit.CraftServer;

import joptsimple.OptionSet;

public interface IMixinMinecraftServer {
	CraftServer getServer();
	OptionSet getOptionSet();
}
