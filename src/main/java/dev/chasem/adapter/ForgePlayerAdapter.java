package dev.chasem.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gg.inventories.InventoriesCore;
import gg.inventories.adapters.player.PlayerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static dev.chasem.adapter.ForgeItemAdapter.airJson;

public class ForgePlayerAdapter extends PlayerAdapter<EntityPlayerMP, ForgeItemAdapter> {

    @Override
    public JsonObject toJson(EntityPlayerMP player) {
        JsonObject playerInfo = new JsonObject();

        playerInfo.addProperty("username", player.getName());
        playerInfo.addProperty("displayName", player.getDisplayNameString());
        playerInfo.addProperty("ping", player.ping); // TODO
        playerInfo.addProperty("uuid", player.getUniqueID().toString());

        playerInfo.addProperty("level", player.experienceLevel);

        playerInfo.addProperty("exp", (player.experience / (player.xpBarCap() * 1.00)));
        playerInfo.addProperty("totalExp", player.experienceTotal);
        playerInfo.addProperty("expToLevel", player.experience);

        playerInfo.addProperty("health", player.getHealth());
        playerInfo.addProperty("hunger", player.getFoodStats().getFoodLevel());

        JsonArray inventoryJson = new JsonArray();

        for (int i = 0; i < player.inventory.armorInventory.size(); i++) {
            ItemStack item = player.inventory.armorInventory.get(i);

            if (!item.isEmpty() && item.getItem() != Items.AIR) {
                inventoryJson.add(this.getItemAdapter().toJson(item));
            } else {
                inventoryJson.add(airJson);
            }
        }
        InventoriesCore.getLogger().fine("Armor of " + player.getName() + " logged.");

        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack item = player.inventory.mainInventory.get(i);

            if (!item.isEmpty() && item.getItem() != Items.AIR) {
                inventoryJson.add(this.getItemAdapter().toJson(item));
            } else {
                inventoryJson.add(airJson);
            }
        }
        InventoriesCore.getLogger().fine("Inventory of " + player.getName() + " logged.");

        if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() != Items.AIR) {
            inventoryJson.add(getItemAdapter().toJson(player.getHeldItemOffhand()));
        } else {
            inventoryJson.add(airJson);
        }
        InventoriesCore.getLogger().fine("Offhand of " + player.getName() + " logged.");

        JsonArray enderInventoryJson = new JsonArray();

        for (int i = 0; i < player.getInventoryEnderChest().getSizeInventory(); i++) {
            ItemStack item = player.getInventoryEnderChest().getStackInSlot(i);

            if (!item.isEmpty() && item.getItem() != Items.AIR) {
                enderInventoryJson.add(this.getItemAdapter().toJson(item));
            } else {
                enderInventoryJson.add(airJson);
            }
        }
        InventoriesCore.getLogger().fine("Enderchest of " + player.getName() + " logged.");

        playerInfo.add("inventory", inventoryJson);

        playerInfo.add("enderChest", enderInventoryJson);
        InventoriesCore.getLogger().info("Syncing " + player.getName());
        return playerInfo;
    }

    @Override
    public ForgeItemAdapter getItemAdapter() {
        return new ForgeItemAdapter();
    }
}
