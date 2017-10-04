package ru.crutch.mixin.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.entity.player.IMixinEntityPlayerMP;
import ru.crutch.interfaces.network.IMixinNetHandlerPlayServer;

import java.util.concurrent.ExecutionException;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer implements IMixinNetHandlerPlayServer {
    @Shadow
    private EntityPlayerMP playerEntity;
    @Shadow
    private MinecraftServer serverController;
    @Shadow private NetworkManager netManager;

    @Override
    public void chat(String s, boolean async) {
        if (s.isEmpty() || this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
        }
        if (!async && s.startsWith("/")) {
            this.handleSlashCommand(s);
        }
        else if (this.playerEntity.getChatVisibility() != EntityPlayer.EnumChatVisibility.SYSTEM) {
            final Player player = this.getPlayer();
            final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet(this.serverController));
            this.server.getPluginManager().callEvent(event);
            if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length != 0) {
                final PlayerChatEvent queueEvent = new PlayerChatEvent(player, event.getMessage(), event.getFormat(), event.getRecipients());
                queueEvent.setCancelled(event.isCancelled());
                final Waitable waitable = new Waitable() {
                    @Override
                    protected Object evaluate() {
                        Bukkit.getPluginManager().callEvent(queueEvent);
                        if (queueEvent.isCancelled()) {
                            return null;
                        }
                        final String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
                        NetHandlerPlayServer.this.serverController.console.sendMessage(message);
                        if (((LazyPlayerSet)queueEvent.getRecipients()).isLazy()) {
                            for (final Object player : NetHandlerPlayServer.this.serverController.getPlayerList().playerEntityList) {
                                ((EntityPlayerMP)player).sendMessage(CraftChatMessage.fromString(message));
                            }
                        }
                        else {
                            for (final Player player2 : queueEvent.getRecipients()) {
                                player2.sendMessage(message);
                            }
                        }
                        return null;
                    }
                };
                if (async) {
                    this.serverController.processQueue.add(waitable);
                }
                else {
                    waitable.run();
                }
                try {
                    waitable.get();
                    return;
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
                catch (ExecutionException e) {
                    throw new RuntimeException("Exception processing chat event", e.getCause());
                }
            }
            if (event.isCancelled()) {
                return;
            }
            s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
            this.serverController.console.sendMessage(s);
            if (((LazyPlayerSet)event.getRecipients()).isLazy()) {
                for (final Object recipient : this.serverController.getPlayerList().playerEntityList) {
                    ((EntityPlayerMP)recipient).sendMessage(CraftChatMessage.fromString(s));
                }
            }
            else {
                for (final Player recipient2 : event.getRecipients()) {
                    recipient2.sendMessage(s);
                }
            }
        }
    }

    private void handleSlashCommand(String s) {
    }

    @Override
    public final boolean isDisconnected() {
        return !((IMixinEntityPlayerMP) playerEntity).isJoining() && !this.netManager.isChannelOpen();
    }

}
