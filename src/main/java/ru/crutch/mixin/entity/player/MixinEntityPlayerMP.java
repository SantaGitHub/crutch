package ru.crutch.mixin.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import ru.crutch.interfaces.entity.IMixinEntityLivingBase;
import ru.crutch.interfaces.entity.player.IMixinEntityPlayerMP;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer implements IMixinEntityPlayerMP {

	public MixinEntityPlayerMP(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
		this.displayName = this.getName();
		this.maxHealthCache = this.getMaxHealth();

	}

	private int newExp = 0;
	private int newLevel = 0;
	private int newTotalExp = 0;
	private boolean keepLevel = false;
	private double maxHealthCache;
	private boolean joining = true;

	public String spawnWorld = "";
	public boolean fauxSleeping;
	public String displayName;
    public ITextComponent listName;
    public Location compassTarget;
	public long timeOffset = 0;
	public boolean relativeTime = true;
	public World world;
	public WeatherType weather = null;
	@Shadow
	public NetHandlerPlayServer connection;
	@Shadow
	public int currentWindowId;
	private float pluginRainPosition;
	private float pluginRainPositionPrevious;

	@Override
	public void setNewExp(int i){
		this.newExp = i;
	}
	@Override
	public void setNewLevel(int i){
		this.newLevel = i;
	}
	@Override
	public void setNewTotalExp(int i){
		this.newTotalExp = i;
	}
	@Override
	public void setKeepLevel(boolean flag){
		this.keepLevel = flag;
	}
	@Override
	public int getNewExp(){
		return this.newExp;
	}
	@Override
	public int getNewLevel(){
		return this.newLevel;
	}
	@Override
	public int getNewTotalExp(){
		return this.newTotalExp;
	}
	@Override
	public boolean getKeepLevel(){
		return this.keepLevel;
	}


	@Override
	public void setSpawnWorld(String world){
		this.spawnWorld = world;
	}
	@Override
	public String getSpawnWorld(){
		return this.spawnWorld;
	}

	@Override
	public boolean getFauxSleeping(){
		return this.fauxSleeping;
	}
	@Override
	public void setFauxSleeping(boolean flag){
		this.fauxSleeping = flag;
	}

	@Override
	public long getTimeOffset(){
		return this.timeOffset;
	}
	@Override
	public boolean getRelativeTime(){
		return this.relativeTime;
	}
	@Override
	public void setTimeOffset(long time){
		this.timeOffset = time;
	}
	@Override
	public void setRelativeTime(boolean flag){
		this.relativeTime = flag;
	}

	@Override
	public int nextContainerCounter(){
		this.currentWindowId = this.currentWindowId % 100 + 1;
		return this.currentWindowId;
	}

	@Override
	public String getdisplayName(){
		return this.displayName;
	}
	@Override
	public void setdisplayName(String name){
		this.displayName = name;
	}
	@Override
	public ITextComponent getListName(){
		return this.listName;
	}
	@Override
	public void setListName(ITextComponent listName){
		this.listName = listName;
	}
	@Override
	public Location getCompassTarget(){
		return this.compassTarget;
	}
	@Override
	public void setCompassTarget(Location loc){
		this.compassTarget = loc;
	}

	@Override
	public long getPlayerTime() {
		if (this.relativeTime) {
			// Adds timeOffset to the current server time.
			return this.world.getWorldTime() + this.timeOffset;
		} else {
			// Adds timeOffset to the beginning of this day.
			return this.world.getWorldTime() - (this.world.getWorldTime() % 24000) + this.timeOffset;
		}
	}

	@Override
	public WeatherType getPlayerWeather() {
		return this.weather;
	}

	@Override
	public void setPlayerWeather(WeatherType type, boolean plugin) {
		if (!plugin && this.weather != null) {
			return;
		}

		if (plugin) {
			this.weather = type;
		}

		if (type == WeatherType.DOWNFALL) {
			this.connection.sendPacket(new SPacketChangeGameState(2, 0));
		} else {
			this.connection.sendPacket(new SPacketChangeGameState(1, 0));
		}
	}

	@Override
	public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
		if (this.weather == null) {
			// Vanilla
			if (oldRain != newRain) {
				this.connection.sendPacket(new SPacketChangeGameState(7, newRain));
			}
		} else {
			// Plugin
			if (pluginRainPositionPrevious != pluginRainPosition) {
				this.connection.sendPacket(new SPacketChangeGameState(7, pluginRainPosition));
			}
		}

		if (oldThunder != newThunder) {
			if (weather == WeatherType.DOWNFALL || weather == null) {
				this.connection.sendPacket(new SPacketChangeGameState(8, newThunder));
			} else {
				this.connection.sendPacket(new SPacketChangeGameState(8, 0));
			}
		}
	}

	@Override
	public void tickWeather() {
		if (this.weather == null)
			return;

		pluginRainPositionPrevious = pluginRainPosition;
		if (weather == WeatherType.DOWNFALL) {
			pluginRainPosition += 0.01;
		} else {
			pluginRainPosition -= 0.01;
		}

		pluginRainPosition = MathHelper.clamp(pluginRainPosition, 0.0F, 1.0F);
	}

	@Override
	public void resetPlayerWeather() {
		this.weather = null;
		this.setPlayerWeather(this.world.getWorldInfo().isRaining() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
	}
	@Override
	public double getMaxHealthCache() {
		return maxHealthCache;
	}
	@Override
	public void setMaxHealthCache(double maxHealthCache) {
		this.maxHealthCache = maxHealthCache;
	}
	@Override
	public boolean isJoining() {
		return joining;
	}
	@Override
	public void setJoining(boolean joining) {
		this.joining = joining;
	}
}
