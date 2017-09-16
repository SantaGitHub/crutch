package ru.crutch.mixin.server.dedicated;

import java.util.Properties;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import joptsimple.OptionSet;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraftforge.fml.server.FMLServerHandler;
import ru.crutch.CrutchServer;
import ru.crutch.interfaces.server.IMixinMinecraftServer;

@Mixin(PropertyManager.class)
public abstract class MixinPropertyManager {
	@Shadow
	@Final
	Properties serverProperties;

	@Shadow
	public abstract void saveProperties();

	OptionSet options;

	@Inject(method = "<init>", at = @At("RETURN"))
	void onConstructed(CallbackInfo ci) {
		CrutchServer.debug("Init property manager");
		options = ((IMixinMinecraftServer) FMLServerHandler.instance().getServer()).getOptionSet();
	}

	private <T> T getOverride(String name, T value) {
		if ((this.options != null) && (this.options.has(name))) {
			return (T) this.options.valueOf(name);
		}

		return value;
	}

	@Overwrite
	public String getStringProperty(String key, String defaultValue) {
		if (!this.serverProperties.containsKey(key)) {
			this.serverProperties.setProperty(key, defaultValue);
			this.saveProperties();
			this.saveProperties();
		}

		return getOverride(key, this.serverProperties.getProperty(key, defaultValue));
	}

	@Overwrite
	public int getIntProperty(String key, int defaultValue) {
		try {
			return getOverride(key, Integer.parseInt(this.getStringProperty(key, "" + defaultValue)));
		} catch (Exception var4) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return getOverride(key, defaultValue);
		}
	}

	@Overwrite
	public long getLongProperty(String key, long defaultValue) {
		try {
			return getOverride(key, Long.parseLong(this.getStringProperty(key, "" + defaultValue)));
		} catch (Exception var5) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return defaultValue;
		}
	}

	@Overwrite
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		try {
			return getOverride(key, Boolean.parseBoolean(this.getStringProperty(key, "" + defaultValue)));
		} catch (Exception var4) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return defaultValue;
		}
	}

}
