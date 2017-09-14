package ru.crutch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CrutchServer.MOD_ID, name = CrutchServer.MOD_NAME, version = CrutchServer.VERSION)
public class CrutchServer {
	public static final Logger LOG = LogManager.getLogger();
	public static final boolean DEBUG = true;

	public static void debug(String str) {
		if (DEBUG) {
			LOG.info(str);
		}
	}

	public static final String MOD_ID = "ru.crutch";
	public static final String MOD_NAME = "Crutch";
	public static final String VERSION = "0.0.1";

	@Mod.Instance(MOD_ID)
	public static CrutchServer INSTANCE;

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {

	}
}
