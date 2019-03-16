package net.tnemc.hellconomy.core.item;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.item.data.BannerData;
import net.tnemc.hellconomy.core.item.data.BookData;
import net.tnemc.hellconomy.core.item.data.EnchantStorageData;
import net.tnemc.hellconomy.core.item.data.FireworkData;
import net.tnemc.hellconomy.core.item.data.FireworkEffectData;
import net.tnemc.hellconomy.core.item.data.LeatherData;
import net.tnemc.hellconomy.core.item.data.MapData;
import net.tnemc.hellconomy.core.item.data.SerialPotionData;
import net.tnemc.hellconomy.core.item.data.ShulkerData;
import net.tnemc.hellconomy.core.item.data.SkullData;
import net.tnemc.hellconomy.core.item.data.TropicalFishBucketData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by Daniel on 11/10/2017.
 */
public class SerialItem {

  private Map<String, Integer> enchantments = new HashMap<>();
  private List<String> lore = new ArrayList<>();

  private int slot = 0;
  private Material material;
  private Integer amount = 1;
  private String display = "";
  private short damage = 0;
  private SerialItemData data;

  //Cache=related variables
  private ItemStack stack;

  public SerialItem(ItemStack stack) {
    this(stack, 0);
  }

  public SerialItem(ItemStack item, Integer slot) {
    stack = item;
    material = stack.getType();
    amount = stack.getAmount();
    damage = stack.getDurability();

    if(stack.hasItemMeta()) {
      display = stack.getItemMeta().getDisplayName();
      lore = stack.getItemMeta().getLore();

      if(stack.getItemMeta().hasEnchants()) {

        stack.getItemMeta().getEnchants().forEach(((enchantment, level) ->{
          enchantments.put(enchantment.getName(), level);
        }));
      }
    }
    buildData(stack);
  }

  private void buildData(ItemStack stack) {
    if(isShulker(stack.getType())) {
      //TNE.debug("Is shulker!!");
      //TNE.debug("shulker");
      data = new ShulkerData();
    } else {
      ItemMeta meta = stack.getItemMeta();
      if (meta instanceof PotionMeta) {
        //TNE.debug("Potion");
        data = new SerialPotionData();
      } else if (meta instanceof BookMeta) {
        //TNE.debug("Book");
        data = new BookData();
      } else if (meta instanceof BannerMeta) {
        //TNE.debug("Banner");
        data = new BannerData();
      } else if (meta instanceof LeatherArmorMeta) {
        //TNE.debug("leather");
        data = new LeatherData();
      } else if (meta instanceof SkullMeta) {
        //TNE.debug("skull");
        data = new SkullData();
      } else if (meta instanceof MapMeta) {
        //TNE.debug("map");
        data = new MapData();
      } else if (meta instanceof EnchantmentStorageMeta) {
        //TNE.debug("enchantment");
        data = new EnchantStorageData();
      } else if (meta instanceof FireworkMeta) {
        //TNE.debug("Firework");
        data = new FireworkData();
      } else if (meta instanceof FireworkEffectMeta) {
        //TNE.debug("Firework Effect");
        data = new FireworkEffectData();
      } else if(meta instanceof TropicalFishBucketMeta) {
        //TNE.debug("fish bucket");
        data = new TropicalFishBucketData();
      }
    }
    if(data != null){
      //TNE.debug("Data != null");
      data.initialize(stack);
    }
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public Map<String, Integer> getEnchantments() {
    return enchantments;
  }

  public void setEnchantments(Map<String, Integer> enchantments) {
    this.enchantments = enchantments;
  }

  public List<String> getLore() {
    return lore;
  }

  public void setLore(List<String> lore) {
    this.lore = lore;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public short getDamage() {
    return damage;
  }

  public void setDamage(short damage) {
    this.damage = damage;
  }

  public SerialItemData getData() {
    return data;
  }

  public void setData(SerialItemData data) {
    this.data = data;
  }

  public ItemStack getStack() {
    if(stack == null) {
      stack = new ItemStack(material, amount, damage);
      ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
      if(display != null) {
        meta.setDisplayName(display);
      }
      meta.setLore(lore);
      enchantments.forEach((name, level)->{
        meta.addEnchant(Enchantment.getByName(name), level, true);
      });
      stack.setItemMeta(meta);
      if(data != null) {
        stack = data.build(stack);
      }
    }
    return stack;
  }

  public void setStack(ItemStack stack) {
    this.stack = stack;
  }

  public JSONObject toJSON() {
    //TNE.debug("toJSON");
    JSONObject json = new JSONObject();
    json.put("slot", slot);
    //TNE.debug("slot");
    json.put("material", material.name());
    //TNE.debug("material");
    json.put("amount", amount);
    //TNE.debug("amount");
    if(display != null && !display.equalsIgnoreCase("")) json.put("display", display);
    //TNE.debug("display");
    json.put("damage", damage);
    //TNE.debug("damage");
    if(lore != null && lore.size() > 0) json.put("lore", String.join(",", lore));
    //TNE.debug("lore");

    JSONObject object = new JSONObject();
    enchantments.forEach(object::put);
    //TNE.debug("enchantments obj");
    json.put("enchantments", object);
    //TNE.debug("enchantments");
    if(data != null) {
      json.put("data", data.toJSON());
    }
    return json;
  }

  public String serialize() {
    return toJSON().toJSONString();
  }

  public static SerialItem unserialize(String serialized) throws ParseException {
    return fromJSON((JSONObject)new JSONParser().parse(serialized));
  }

  public static SerialItem fromJSON(JSONObject json) {
    final JSONHelper helper = new JSONHelper(json);
    //TNE.debug("JSON: " + helper.toString());
    //TNE.debug("fromJSON");
    Material material = Material.matchMaterial(helper.getString("material"));
    //TNE.debug("Material: " + material.name());
    ItemStack stack = new ItemStack(material, helper.getInteger("amount"));
    //TNE.debug("Stack Created");
    //TNE.debug("Stack amount");
    ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
    //TNE.debug("Stack meta");
    //TNE.debug("Stack metaz");
    //TNE.debug(json.toJSONString());
    if(helper.has("display") && helper.isNull("display") && !helper.getString("display").equalsIgnoreCase("")) {
      meta.setDisplayName(helper.getString("display"));
    }
    //TNE.debug("Stack display");
    if(helper.has("lore")) meta.setLore(new ArrayList<>(Arrays.asList((helper.getString("lore")).split(","))));
    //TNE.debug("Stack lore");
    stack.setItemMeta(meta);

    if(json.containsKey("enchantments")) {
      //TNE.debug("Enchants: " + json.get("enchantments"));
      JSONObject enchants = (JSONObject)json.get("enchantments");
      enchants.forEach((key, value) -> {
        //TNE.debug("Name: " + key);
        //TNE.debug("Integer: " + value);
        stack.addUnsafeEnchantment(Enchantment.getByName(String.valueOf(key)), Integer.valueOf(String.valueOf(value)));
        //TNE.debug("Enchants Size: " + stack.getEnchantments().size());
      });
    }
    //TNE.debug("Stack enchants");

    stack.setDurability(helper.getShort("damage"));
    SerialItem serial = new SerialItem(stack, helper.getInteger("slot"));
    if(helper.has("data")) {
      serial.getData().readJSON(helper.getHelper("data"));
      serial.getData().build(stack);
    }
    //TNE.debug("Finished reading item's JSON");
    return serial;
  }

  public static boolean isShulker(Material material) {
    return HellConomy.item().isShulker(material);
  }
}