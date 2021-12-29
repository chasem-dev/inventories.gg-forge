package dev.chasem.adapter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import gg.inventories.adapters.items.ItemAdapter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityShulkerBox;
import java.util.List;

public class ForgeItemAdapter extends ItemAdapter<ItemStack> {

    static JsonObject airJson = new JsonObject();

    static {
        airJson.addProperty("type", "ignore?");
        airJson.addProperty("unlocalizedName", Items.AIR.getRegistryName().toString());
        airJson.addProperty("source", Items.AIR.getRegistryName().getResourceDomain());
        airJson.addProperty("itemName", Items.AIR.getRegistryName().getResourcePath());
    }

    @Override
    public JsonObject toJson(ItemStack stack) {
        JsonObject itemJson = new JsonObject();

        // Material type
        itemJson.addProperty("type", "ignore");

        //Fake data for website.
        itemJson.addProperty("unlocalizedName", stack.getItem().getRegistryName().toString());
        itemJson.addProperty("source", stack.getItem().getRegistryName().getResourceDomain());
        itemJson.addProperty("itemName", stack.getItem().getRegistryName().getResourcePath());

        if (stack.getItem() == Items.AIR) {
            return itemJson;
        }

        if (stack.getItemDamage() > 0) {
            itemJson.addProperty("data", stack.getItemDamage());
            itemJson.addProperty("durability", stack.getItemDamage());
        }

        if (stack.getCount() != 1) {
            itemJson.addProperty("amount", stack.getCount());
        }

        if (stack.getMaxDamage() > 0) {
            itemJson.addProperty("maxDurability", stack.getMaxDamage());
        }
//        if (stack.hasItemMeta()) {
        JsonObject metaJson = new JsonObject();

//        ItemMeta meta = stack.getItemMeta();

        if (stack.hasDisplayName()) {
            itemJson.addProperty("displayName", stack.getDisplayName());
        }

        //TODO ?
//        if (meta.hasLocalizedName()) {
//            metaJson.addProperty("localizedName", meta.getLocalizedName());
//        }

        List<String> loreList = getLore(stack);

        if (loreList.size() > 0) {
            JsonArray lore = new JsonArray();
            loreList.forEach(line -> lore.add(new JsonPrimitive(line)));
            metaJson.add("lore", lore);
        }

        if (stack.isItemEnchanted()) {
            JsonArray enchants = new JsonArray();
            //this.stackTagCompound != null ? this.stackTagCompound.getTagList("ench", 10) : new NBTTagList()
            for (NBTBase nbtBase : stack.getEnchantmentTagList()) {
                NBTTagCompound compound = (NBTTagCompound) nbtBase;
                short enchantId = compound.getShort("id");
                short enchantLvl = compound.getShort("lvl");
                Enchantment enchant = Enchantment.getEnchantmentByID(enchantId);
                enchants.add(new JsonPrimitive(enchant.getTranslatedName(enchantLvl)));
            }
//            meta.getEnchants().forEach((enchantment, level) -> enchants.add(new JsonPrimitive(enchantment.getKey().getKey() + ":" + level)));

            metaJson.add("enchants", enchants);
        }

        // TODO FLAGS
//        stack.getItem().
//        if (!meta.getItemFlags().isEmpty()) {
//            JsonArray flags = new JsonArray();
//
//            meta.getItemFlags().forEach(itemFlag -> flags.add(new JsonPrimitive(itemFlag.name())));
//
//            metaJson.add("flags", flags);
//        }

        if (stack.getItem() == Items.SKULL) {
            ItemSkull skull = ((ItemSkull) stack.getItem());
            //TODO SKULL.
//            if (skullMeta.hasOwner()) {
//                JsonObject skullData = new JsonObject();
//                skullData.addProperty("owner", skullMeta.getOwner());
//                skullData.addProperty("metaType", "SKULL");
//
//                metaJson.add("extraMeta", skullData);
//            }
        } else if (stack.getItem() == Items.BANNER) {

//          TODO
//            if (bannerMeta.numberOfPatterns() > 0) {
//                //TODO:
//            }

        } else if (stack.getItem() == Items.ENCHANTED_BOOK) {

            JsonObject esData = new JsonObject();

            esData.addProperty("metaType", "ENCHANTMENT_STORAGE");
            NBTTagList enchantments = ItemEnchantedBook.getEnchantments(stack);
            if (enchantments.tagCount() > 0) {
                JsonArray enchants = new JsonArray();

                for (NBTBase nbtBase : enchantments) {
                    NBTTagCompound compound = (NBTTagCompound) nbtBase;
                    short enchantId = compound.getShort("id");
                    short enchantLvl = compound.getShort("lvl");
                    Enchantment enchant = Enchantment.getEnchantmentByID(enchantId);
                    enchants.add(new JsonPrimitive(enchant.getName() + ":" + enchantLvl));
                }
                esData.add("storedEnchants", enchants);
            }

            metaJson.add("extraMeta", esData);

//        }
//        else if (meta instanceof BookMeta bookMeta) {
//
//            JsonObject bookData = new JsonObject();
//            bookData.addProperty("metaType", "BOOK_META");
//
//            if (bookMeta.hasAuthor() || bookMeta.hasPages() || bookMeta.hasTitle()) {
//                if (bookMeta.hasTitle()) {
//                    bookData.addProperty("title", bookMeta.getTitle());
//                }
//
//                if (bookMeta.hasAuthor()) {
//                    bookData.addProperty("author", bookMeta.getAuthor());
//                }
//
//                if (bookMeta.hasPages()) {
//                    JsonArray pages = new JsonArray();
//                    bookMeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
//                    bookData.add("pages", pages);
//                }
//            }
//
//            metaJson.add("extraMeta", bookData);
//
//        } else if (meta instanceof FireworkMeta) {
//
//        } else if (meta instanceof FireworkEffectMeta) {

        } else if (stack.getItem() == Items.POTIONITEM || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
            JsonObject potionData = new JsonObject();
            potionData.addProperty("metaType", "POTION_META");

            PotionType potion = PotionUtils.getPotionFromItem(stack);

            boolean potionUpgraded = false;
            for (PotionEffect effect : potion.getEffects()) {
                System.out.println("POTION : " + effect.getAmplifier());
                if (effect.getAmplifier() > 1) {
                    potionUpgraded = true;
                    break;
                }
            }

            potionData.addProperty("potionType", potion.getNamePrefixed(""));
            potionData.addProperty("potionLevel", potionUpgraded ? 2 : 1);

            // TODO
//            if (potionMeta.hasCustomEffects()) {
//                JsonArray customEffects = new JsonArray();
//                potionMeta.getCustomEffects().forEach(potionEffect -> {
//                    customEffects.add(new JsonPrimitive(potionEffect.getType().getName()
//                            + ":" + potionEffect.getAmplifier()
//                            + ":" + potionEffect.getDuration() / 20));
//                });
//
//                potionData.add("customEffects", customEffects);
//            }

            metaJson.add("extraMeta", potionData);
//        } else if (meta instanceof MapMeta) {
//
//        } else if (meta instanceof CrossbowMeta) {
//
//        } else if (meta instanceof TropicalFishBucketMeta) {
//
//        } else if (meta instanceof SpawnEggMeta) {
//
//        } else if (meta instanceof LeatherArmorMeta) {

        } else if (stack.getItem().getRegistryName().getResourcePath().contains("shulker")) {
            JsonObject blockStateJson = new JsonObject();

            blockStateJson.addProperty("metaType", "SHULKER_BOX");
            JsonArray shulkerJson = new JsonArray();

            if (stack.getTagCompound().hasKey("BlockEntityTag")) {
                NBTTagCompound blocktag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
                TileEntityShulkerBox box = new TileEntityShulkerBox();
                box.loadFromNbt(blocktag);

                for (int i = 0; i < box.getSizeInventory(); i++) {
                    ItemStack shulkerStack = box.getStackInSlot(i);
                    if (shulkerStack == null) {
                        shulkerJson.add(airJson);
                    } else {
                        shulkerJson.add(this.toJson(shulkerStack));
                    }
                }

            }
            blockStateJson.add("inventory", shulkerJson);

            metaJson.add("extraMeta", blockStateJson);
        }
        itemJson.add("itemMeta", metaJson);
//        }

        if (!itemJson.has("displayName")) {
            itemJson.addProperty("displayName", stack.getItem().getItemStackDisplayName(stack));
        }

        //TODO: Item json

        return itemJson;
    }

    public List<String> getLore(ItemStack stack) {
        ImmutableList.Builder<String> loreBuilder = ImmutableList.builder();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Lore")) {
            NBTTagList loreNbt = stack.getTagCompound().getTagList("Lore", 8);
            for (int i = 0; i < loreNbt.tagCount(); i++) {
                NBTTagString currentLore = (NBTTagString) loreNbt.get(i);
                loreBuilder.add(currentLore.getString());
            }
        }
        List<String> lore = loreBuilder.build();

        return lore;
    }
}
