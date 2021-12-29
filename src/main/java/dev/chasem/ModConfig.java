package dev.chasem;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = InventoriesForge.MODID)
@Config.LangKey("inventories.config.title")
public class ModConfig {

    @Config.Comment("Please locate your client secret on your server dashboard @ inventories.gg/dashboard")
    public static String clientId = "<INSERT CLIENT ID>";


    @Mod.EventBusSubscriber(modid = InventoriesForge.MODID)
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(InventoriesForge.MODID)) {
                ConfigManager.sync(InventoriesForge.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
