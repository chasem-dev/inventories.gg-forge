package dev.chasem;

import dev.chasem.adapter.ForgePlayerAdapter;
import gg.inventories.InventoriesCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = InventoriesForge.MODID, name = InventoriesForge.NAME, version = InventoriesForge.VERSION, acceptableRemoteVersions = "*", serverSideOnly = true)
public class InventoriesForge {
    public static final String MODID = "inventories.gg";
    public static final String NAME = "Inventories.gg";
    public static final String VERSION = "1.0";

    private static Logger logger;
    private final ForgePlayerAdapter playerAdapter = new ForgePlayerAdapter();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
//        InventoriesCore.API_URL = "http://localhost:3000/api";
    }

    private String getClientSecret() {
        return ModConfig.clientId;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        InventoriesCore.getLogger().info("Inventories.gg Forge Mod Enabled.");
        MinecraftForge.EVENT_BUS.register(this);
        if (getClientSecret() == null) {
            try {
                throw (new Exception("Failed to Start Inventories.gg... Missing clientSecret in config."));
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        } else {
            InventoriesCore.setClientSecret(getClientSecret());
        }
    }

    @EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    syncPlayer(player);
                }
            }
        }, 0, 1000 * 60 * 2); // 2 minutes
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayer((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        syncPlayer((EntityPlayerMP) event.player);
    }

    public void syncPlayer(EntityPlayerMP player) {
        new Thread(() -> InventoriesCore.sendUpdateRequest(playerAdapter.toJson(player))).start();
    }

}
