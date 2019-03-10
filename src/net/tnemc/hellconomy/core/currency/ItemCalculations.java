package net.tnemc.hellconomy.core.currency;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.utils.MISCUtils;
import net.tnemc.hellconomy.core.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

/**
 * HellConomy Minecraft Server Plugin
 * <p>
 * Created by Daniel on 6/3/2018.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class ItemCalculations {

  public static void clearItems(HellCurrency currency, Inventory inventory) {
    for(HellTier tier : currency.getHellTiers()) {
      removeAllItem(tier.getItemInfo().toStack(), inventory);
    }
  }

  public static void removeAllItem(ItemStack stack, Inventory inventory) {
    for(int i = 0; i < inventory.getContents().length; i++) {
      final ItemStack item = inventory.getItem(i);

      if(stack != null && MaterialUtils.itemsEqual(stack, item)) inventory.setItem(i, null);
    }
  }

  public static Integer getCount(ItemStack stack, Inventory inventory) {
    ItemStack compare = stack.clone();
    compare.setAmount(1);

    Integer value = 0;
    for(ItemStack item : inventory.getContents()) {
      if(MaterialUtils.itemsEqual(compare, item)) {
        value += item.getAmount();
      }
    }
    return value;
  }

  public static void setItems(HellCurrency currency, BigDecimal amount, Inventory inventory, boolean remove) {
    if(currency.isItem()) {
      BigDecimal old = getCurrencyItems(currency, inventory);
      BigDecimal difference = (amount.compareTo(old) >= 0)? amount.subtract(old) : old.subtract(amount);
      if(remove) difference = amount;
      String differenceString = difference.toPlainString();
      String[] split = (differenceString + (differenceString.contains(".")? "" : ".00")).split("\\.");
      boolean consolidate = HellConomy.mapper().getBool("currency.consolidate");
      boolean add = (consolidate) || amount.compareTo(old) >= 0;
      if(remove) add = false;

      if(consolidate) split = (amount.toPlainString() + (amount.toPlainString().contains(".")? "" : ".00")).split("\\.");

      if(consolidate) clearItems(currency, inventory);
      BigInteger majorChange = (consolidate)? setMajorConsolidate(currency, new BigInteger(split[0]), inventory) :
          setMajor(currency, new BigInteger(split[0]), add, inventory);
      BigInteger minorChange = (consolidate)? setMinorConsolidate(currency, new BigInteger(split[1]), inventory) :
          setMinor(currency, new BigInteger(split[1]), add, inventory);

      if(!consolidate && !add) {
        if(majorChange.compareTo(BigInteger.ZERO) > 0) {
          setMajor(currency, majorChange, true, inventory);
        }

        if(minorChange.compareTo(BigInteger.ZERO) > 0) {
          setMinor(currency, minorChange, true, inventory);
        }
      }
    }
  }

  public static BigInteger setMajorConsolidate(HellCurrency currency, BigInteger amount, Inventory inventory) {
    Map<Integer, ItemStack> items = new HashMap<>();
    BigInteger workingAmount = new BigInteger(amount.toString());
    for(Map.Entry<Integer, HellTier> entry : currency.getTNEMajorTiers().entrySet()) {
      BigInteger weight = BigInteger.valueOf(entry.getKey());

      BigInteger itemAmount = workingAmount.divide(weight);

      workingAmount = workingAmount.subtract(weight.multiply(itemAmount));
      ItemStack stack = entry.getValue().getItemInfo().toStack();
      stack.setAmount(itemAmount.intValue());
      items.put(entry.getKey(), stack);
    }
    final int dropped = giveItemsFeedback(items.values(), inventory);
    if(dropped > 0) {
      if(inventory.getHolder() instanceof HumanEntity) {
        ((HumanEntity) inventory.getHolder()).sendMessage(ChatColor.RED + "Your inventory was full, check the ground for excess currency items.");
      }
    }
    return BigInteger.ZERO;
  }

  public static BigInteger setMinorConsolidate(HellCurrency currency, BigInteger amount, Inventory inventory) {
    Map<Integer, ItemStack> items = new HashMap<>();
    BigInteger workingAmount = new BigInteger(amount.toString());
    for(Map.Entry<Integer, HellTier> entry : currency.getTNEMinorTiers().entrySet()) {
      BigInteger weight = BigInteger.valueOf(entry.getKey());

      BigInteger itemAmount = workingAmount.divide(weight);

      workingAmount = workingAmount.subtract(weight.multiply(itemAmount));
      ItemStack stack = entry.getValue().getItemInfo().toStack();
      stack.setAmount(itemAmount.intValue());
      items.put(entry.getKey(), stack);
    }
    final int dropped = giveItemsFeedback(items.values(), inventory);
    if(dropped > 0) {
      if(inventory.getHolder() instanceof HumanEntity) {
        ((HumanEntity) inventory.getHolder()).sendMessage(ChatColor.RED + "Your inventory was full, check the ground for excess currency items.");
      }
    }
    return BigInteger.ZERO;
  }

  public static BigInteger setMajor(HellCurrency currency, BigInteger amount, boolean add, Inventory inventory) {
    Map<Integer, ItemStack> items = new HashMap<>();
    BigInteger workingAmount = new BigInteger(amount.toString());
    BigInteger actualAmount = BigInteger.ZERO;
    NavigableMap<Integer, HellTier> values = (add)? currency.getTNEMajorTiers() :
        currency.getTNEMajorTiers().descendingMap();
    String additional = "0";
    for(Map.Entry<Integer, HellTier> entry : values.entrySet()) {
      if(entry.getKey() <= 0) continue;
      BigInteger weight = BigInteger.valueOf(entry.getKey());

      BigInteger itemAmount = workingAmount.divide(weight).add(new BigInteger(additional));
      BigInteger itemActual = new BigInteger(getCount(entry.getValue().getItemInfo().toStack(), inventory) + "");
      additional = "0";

      if(!add && itemActual.compareTo(itemAmount) < 0) {
        additional = itemAmount.subtract(itemActual).toString();
        itemAmount = itemActual;
      }

      ItemStack stack = entry.getValue().getItemInfo().toStack();
      stack.setAmount(itemAmount.intValue());


      actualAmount = actualAmount.add(weight.multiply(itemAmount));
      workingAmount = workingAmount.subtract(weight.multiply(itemAmount));
      items.put(entry.getKey(), stack);
    }
    if(add) {
      final int dropped = giveItemsFeedback(items.values(), inventory);
      if(dropped > 0) {
        if(inventory.getHolder() instanceof HumanEntity) {
          ((HumanEntity) inventory.getHolder()).sendMessage(ChatColor.RED + "Your inventory was full, check the ground for excess currency items.");
        }
      }
    } else takeItems(items.values(), inventory);

    if(actualAmount.compareTo(amount) > 0) {
      return actualAmount.subtract(amount);
    }
    return BigInteger.ZERO;
  }

  public static BigInteger setMinor(HellCurrency currency, BigInteger amount, boolean add, Inventory inventory) {
    Map<Integer, ItemStack> items = new HashMap<>();
    BigInteger workingAmount = new BigInteger(amount.toString());
    BigInteger actualAmount = BigInteger.ZERO;
    NavigableMap<Integer, HellTier> values = (add)? currency.getTNEMinorTiers() :
        currency.getTNEMinorTiers().descendingMap();
    String additional = "0";
    for(Map.Entry<Integer, HellTier> entry : values.entrySet()) {
      BigInteger weight = BigInteger.valueOf(entry.getKey());

      BigInteger itemAmount = workingAmount.divide(weight).add(new BigInteger(additional));
      BigInteger itemActual = new BigInteger(getCount(entry.getValue().getItemInfo().toStack(), inventory) + "");
      additional = "0";

      if(!add && itemActual.compareTo(itemAmount) < 0) {
        additional = itemAmount.subtract(itemActual).toString();
        itemAmount = itemActual;
      }

      ItemStack stack = entry.getValue().getItemInfo().toStack();
      stack.setAmount(itemAmount.intValue());


      actualAmount = actualAmount.add(weight.multiply(itemAmount));
      workingAmount = workingAmount.subtract(weight.multiply(itemAmount));
      items.put(entry.getKey(), stack);
    }
    if(add) {

      final int dropped = giveItemsFeedback(items.values(), inventory);
      if(dropped > 0) {
        if(inventory.getHolder() instanceof HumanEntity) {
          ((HumanEntity) inventory.getHolder()).sendMessage(ChatColor.RED + "Your inventory was full, check the ground for excess currency items.");
        }
      }
    } else takeItems(items.values(), inventory);

    if(!add && workingAmount.compareTo(BigInteger.ZERO) > 0) {
      BigInteger minor = new BigInteger(currency.getMinorWeight() - workingAmount.intValue() + "");
      setMajor(currency, BigInteger.ONE, false, inventory);
      setMinor(currency, minor, true, inventory);
    }

    if(add) {
      final BigInteger minorAmount = getCurrencyItems(currency, inventory, "minor").toBigInteger();
      if (minorAmount.intValue() >= currency.getMinorWeight()) {
        int major = minorAmount.intValue() / currency.getMinorWeight();
        setMajor(currency, BigInteger.valueOf(major), true, inventory);
        setMinor(currency, BigInteger.valueOf(currency.getMinorWeight() * major), false, inventory);
      }
    }

    if(actualAmount.compareTo(amount) > 0) {
      return actualAmount.subtract(amount);
    }
    return BigInteger.ZERO;
  }

  public static BigDecimal getCurrencyItems(HellCurrency currency, Inventory inventory) {
    return getCurrencyItems(currency, inventory, "all");
  }

  public static BigDecimal getCurrencyItems(HellCurrency currency, Inventory inventory, String type) {
    BigDecimal value = BigDecimal.ZERO;
    BigInteger minor = BigInteger.ZERO;

    if(currency.isItem()) {
      if(type.equalsIgnoreCase("all") || type.equalsIgnoreCase("major")) {
        for (HellTier tier : currency.getTNEMajorTiers().values()) {
          value = value.add(new BigDecimal(getCount(tier.getItemInfo().toStack(), inventory) * tier.weight()));
        }
      }

      if(type.equalsIgnoreCase("all") || type.equalsIgnoreCase("minor")) {
        for (HellTier tier : currency.getTNEMinorTiers().values()) {
          Integer parsed = (getCount(tier.getItemInfo().toStack(), inventory) * tier.weight());
          //String convert = "." + String.format(Locale.US, "%0" + currency.decimalPlaces() + "d", parsed);
          minor = minor.add(new BigInteger(parsed + ""));
        }
        if(type.equalsIgnoreCase("minor")) {
          return new BigDecimal("." + String.format(Locale.US, "%0" + currency.decimalPlaces() + "d", minor.intValue()));
        }
        //System.out.println("Value: " + value.toPlainString());
        //System.out.println("Minor: " + minor.toString());
        final BigInteger major = minor.divide(new BigInteger(currency.getMinorWeight() + ""));
        //System.out.println("Major: " + major.toString());
        minor = minor.subtract(major.multiply(new BigInteger(currency.getMinorWeight() + "")));
        //System.out.println("Minor: " + minor.toString());
        value = value.add(new BigDecimal("." + String.format(Locale.US, "%0" + currency.decimalPlaces() + "d", minor.intValue())));
        value = value.add(new BigDecimal(major.toString()));
        //System.out.println("Value: " + value.toPlainString());
      }
    }

    return value;
  }

  public static void takeItems(Collection<ItemStack> items, Inventory inventory) {
    for (ItemStack item : items) {
      removeItem(item, inventory);
    }
  }

  public static int giveItemsFeedback(Collection<ItemStack> items, Inventory inventory) {
    int leftAmount = 0;
    for(ItemStack item : items) {
      Map<Integer, ItemStack> left = inventory.addItem(item);

      if(left.size() > 0) {
        if(inventory instanceof PlayerInventory) {
          final HumanEntity entity = ((HumanEntity)inventory.getHolder());
          for (Map.Entry<Integer, ItemStack> entry : left.entrySet()) {
            final ItemStack i = entry.getValue();
            Bukkit.getScheduler().runTask(HellConomy.instance(), () -> {
              try {
                entity.getWorld().dropItemNaturally(entity.getLocation(), i);
              } catch(Exception e) {
                //attempted to drop air/some crazy/stupid error.
              }
            });
          }
        }
        leftAmount++;
      }
    }
    return 0;
  }

  public static void giveItems(Collection<ItemStack> items, Inventory inventory) {
    for(ItemStack item : items) {
      Map<Integer, ItemStack> left = inventory.addItem(item);

      if(left.size() > 0) {
        if(inventory instanceof PlayerInventory) {
          final HumanEntity entity = ((HumanEntity)inventory.getHolder());
          for (Map.Entry<Integer, ItemStack> entry : left.entrySet()) {
            final ItemStack i = entry.getValue();
            Bukkit.getScheduler().runTask(HellConomy.instance(), () -> {
              try {
                entity.getWorld().dropItemNaturally(entity.getLocation(), i);
              } catch(Exception e) {
                //attempted to drop air/some crazy/stupid error.
              }
            });
          }
          entity.sendMessage(ChatColor.RED + "Your inventory was full so some items were dropped on the ground.");
        }
      }
    }
  }

  public static void giveItem(ItemStack stack, Inventory inventory, final Integer amount) {
    int remaining = amount;
    final int stacks = (amount > stack.getMaxStackSize())? (int)Math.ceil(amount / stack.getMaxStackSize()) : 1;

    Collection<ItemStack> items = new ArrayList<>();
    for(int i = 0; i < stacks; i++) {
      ItemStack clone = stack.clone();
      if(i == stacks - 1) {
        clone.setAmount(remaining);
      } else {
        clone.setAmount(stack.getMaxStackSize());
      }
      items.add(clone);
      remaining = remaining - clone.getAmount();
    }
    giveItems(items, inventory);
  }

  public static Integer removeItemAmount(ItemStack stack, Inventory inventory, final Integer amount) {
    int left = amount;

    for(int i = 0; i < inventory.getStorageContents().length; i++) {
      if(left <= 0) break;
      ItemStack item = inventory.getItem(i);
      if(item == null || !MaterialUtils.itemsEqual(stack, item)) continue;

      if(item.getAmount() <= left) {
        left -= item.getAmount();
        inventory.setItem(i, null);
      } else {
        item.setAmount(item.getAmount() - left);
        inventory.setItem(i, item);
        left = 0;
      }
    }

    if(left > 0 && inventory instanceof PlayerInventory) {
      final ItemStack helmet = ((PlayerInventory) inventory).getHelmet();
      if(helmet != null && helmet.isSimilar(stack)) {
        if(helmet.getAmount() <= left) {
          left -= helmet.getAmount();
          ((PlayerInventory) inventory).setHelmet(null);
        } else {
          helmet.setAmount(helmet.getAmount() - left);
          ((PlayerInventory) inventory).setHelmet(helmet);
          left = 0;
        }
      }

      if(left > 0 && MISCUtils.offHand()) {
        final ItemStack hand = ((PlayerInventory) inventory).getItemInOffHand();

        if(hand != null && hand.isSimilar(stack)) {
          if (hand.getAmount() <= left) {
            left -= hand.getAmount();
            ((PlayerInventory) inventory).setItemInOffHand(null);
          } else {
            hand.setAmount(hand.getAmount() - left);
            ((PlayerInventory) inventory).setItemInOffHand(hand);
            left = 0;
          }
        }
      }
    }
    return left;
  }

  public static Integer removeItem(ItemStack stack, Inventory inventory) {

    int left = stack.clone().getAmount();

    for(int i = 0; i < inventory.getStorageContents().length; i++) {
      if(left <= 0) break;
      ItemStack item = inventory.getItem(i);
      if(item == null || !MaterialUtils.itemsEqual(stack, item)) continue;

      if(item.getAmount() <= left) {
        left -= item.getAmount();
        inventory.setItem(i, null);
      } else {
        item.setAmount(item.getAmount() - left);
        inventory.setItem(i, item);
        left = 0;
      }
    }

    if(left > 0 && inventory instanceof PlayerInventory) {
      final ItemStack helmet = ((PlayerInventory) inventory).getHelmet();
      if(helmet != null && helmet.isSimilar(stack)) {
        if(helmet.getAmount() <= left) {
          left -= helmet.getAmount();
          ((PlayerInventory) inventory).setHelmet(null);
        } else {
          helmet.setAmount(helmet.getAmount() - left);
          ((PlayerInventory) inventory).setHelmet(helmet);
          left = 0;
        }
      }

      if(left > 0 && MISCUtils.offHand()) {
        final ItemStack hand = ((PlayerInventory) inventory).getItemInOffHand();

        if(hand != null && hand.isSimilar(stack)) {
          if (hand.getAmount() <= left) {
            left -= hand.getAmount();
            ((PlayerInventory) inventory).setItemInOffHand(null);
          } else {
            hand.setAmount(hand.getAmount() - left);
            ((PlayerInventory) inventory).setItemInOffHand(hand);
            left = 0;
          }
        }
      }
    }
    return left;
  }
}