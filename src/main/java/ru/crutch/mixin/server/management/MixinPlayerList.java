package ru.crutch.mixin.server.management;

import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.Bukkit;
import org.bukkit.Server;
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
import net.minecraftforge.fml.server.FMLServerHandler;
import ru.crutch.CrutchServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;
import ru.crutch.interfaces.server.management.IMixinPlayerList;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements IMixinPlayerList {

	@Shadow @Final
	public List<EntityPlayerMP> playerEntityList;
}
