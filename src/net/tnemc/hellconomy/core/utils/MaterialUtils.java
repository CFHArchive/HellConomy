package net.tnemc.hellconomy.core.utils;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.currency.ItemTier;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MaterialUtils {

  public static Boolean itemsEqual(ItemStack original, ItemStack compare) {
    if(compare == null) return false;
    ItemMeta originalMeta = original.getItemMeta();
    ItemMeta compareMeta = compare.getItemMeta();
    if(compare.hasItemMeta()) {
      if (compareMeta.hasDisplayName()) {
        if (!originalMeta.hasDisplayName()) return false;
        if (!originalMeta.getDisplayName().equalsIgnoreCase(compareMeta.getDisplayName())) return false;
      }
      if (compareMeta.hasLore()) {
        if (!originalMeta.hasLore()) return false;
        if (!originalMeta.getLore().containsAll(compareMeta.getLore())) return false;
      }

      if (compareMeta.hasEnchants()) {
        if (!originalMeta.hasEnchants()) return false;

        for (Map.Entry<Enchantment, Integer> entry : compare.getEnchantments().entrySet()) {
          if (!original.containsEnchantment(entry.getKey())) return false;
          if (original.getEnchantmentLevel(entry.getKey()) != entry.getValue()) return false;
        }
      }
    }

    if(isShulker(original.getType())) {
      if(originalMeta instanceof BlockStateMeta && compareMeta instanceof  BlockStateMeta) {
        BlockStateMeta state = (BlockStateMeta) originalMeta;
        BlockStateMeta stateCompare = (BlockStateMeta) compareMeta;
        if (state.getBlockState() instanceof ShulkerBox && stateCompare.getBlockState() instanceof ShulkerBox) {
          ShulkerBox shulker = (ShulkerBox)state.getBlockState();
          ShulkerBox shulkerCompare = (ShulkerBox)stateCompare.getBlockState();

          for(int i = 0; i < shulker.getInventory().getSize(); i++) {
            final ItemStack stack = shulker.getInventory().getItem(i);
            if(stack != null) {
              if(!MaterialUtils.itemsEqual(stack, shulkerCompare.getInventory().getItem(i))) return false;
            }
          }
          return true;
        }
        return false;
      }
      return false;
    } else if(original.getType().equals(Material.WRITTEN_BOOK) ||
        original.getType().equals(Material.WRITABLE_BOOK)) {
      if(originalMeta instanceof BookMeta && compareMeta instanceof BookMeta) {
        return new SerialItem(original).serialize().equals(new SerialItem(compare).serialize());
      }
      return false;
    } else if(original.getType().equals(Material.ENCHANTED_BOOK)) {
      if(originalMeta instanceof EnchantmentStorageMeta && compareMeta instanceof  EnchantmentStorageMeta) {
        return new SerialItem(original).serialize().equals(new SerialItem(compare).serialize());
      }
      return false;
    }
    if(!original.getType().equals(compare.getType())) return false;
    if(original.getDurability() != compare.getDurability()) return false;
    return true;
  }

  public static boolean isShulker(Material material) {
    return HellConomy.item().isShulker(material);
  }

  public static void removeItem(Player player, ItemTier info) {

    List<Integer> remove = new ArrayList<>();

    for(int i = 0; i < player.getInventory().getContents().length; i++) {
      ItemStack stack = player.getInventory().getItem(i);

      if(itemsEqual(info.toStack(), stack)) remove.add(i);
    }

    for(Integer i : remove) {
      player.getInventory().setItem(i, null);
    }
  }

  public static Integer getCount(Player player, ItemTier info) {

    Integer value = 0;
    for(ItemStack stack : player.getInventory().getContents()) {
      if(itemsEqual(info.toStack(), stack)) {
        value += stack.getAmount();
      }
    }
    return value;
  }

  public static Integer getCount(Player player, ItemTier info, PlayerInventory inventory) {
    Inventory inv = (inventory == null)? player.getInventory() : inventory;

    Integer value = 0;
    for(ItemStack stack : inv.getContents()) {
      if(itemsEqual(info.toStack(), stack)) {
        value += stack.getAmount();
      }
    }
    return value;
  }
}