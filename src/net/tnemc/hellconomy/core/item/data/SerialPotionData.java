package net.tnemc.hellconomy.core.item.data;


import net.tnemc.hellconomy.core.item.JSONHelper;
import net.tnemc.hellconomy.core.item.SerialItemData;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by Daniel on 11/10/2017.
 */
public class SerialPotionData implements SerialItemData {

  private List<PotionEffect> customEffects = new ArrayList<>();
  private String type;
  private Color color;
  private boolean extended;
  private boolean upgraded;

  private boolean valid = false;

  @Override
  public SerialItemData initialize(ItemStack stack) {
    ItemMeta meta = stack.getItemMeta();
    if(meta instanceof PotionMeta) {
      valid = true;
      PotionMeta potionMeta = (PotionMeta)meta;

      if(potionMeta.hasColor()) color = potionMeta.getColor();
      customEffects = potionMeta.getCustomEffects();
      type = potionMeta.getBasePotionData().getType().name();
      extended = potionMeta.getBasePotionData().isExtended();
      upgraded = potionMeta.getBasePotionData().isUpgraded();
    }
    return this;
  }

  @Override
  public ItemStack build(ItemStack stack) {
    if(valid) {
      PotionMeta meta = (PotionMeta)stack.getItemMeta();
      if(color != null) meta.setColor(color);
      customEffects.forEach((effect)->meta.addCustomEffect(effect, true));
      PotionData data = new PotionData(PotionType.valueOf(type), extended, upgraded);
      meta.setBasePotionData(data);
      stack.setItemMeta(meta);
    }
    return stack;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("name", "potion");
    json.put("type", type);
    if(color != null) json.put("colour", color.asRGB());
    json.put("extended", extended);
    json.put("upgraded", upgraded);

    if(customEffects.size() > 0) {
      JSONObject effects = new JSONObject();
      for(PotionEffect effect : customEffects) {
        JSONObject effObject = new JSONObject();
        effObject.put("name", effect.getType().getName());
        effObject.put("amplifier", effect.getAmplifier());
        effObject.put("duration", effect.getDuration());
        effObject.put("ambient", effect.isAmbient());
        effObject.put("particles", effect.hasParticles());
        effObject.put("icon", effect.hasIcon());
        effects.put(effect.getType().getName(), effObject);
      }
      json.put("effects", effects);
    }
    return json;
  }

  @Override
  public void readJSON(JSONHelper json) {
    valid = true;
    //TNE.debug("Potion Data Reading Start");
    type = json.getString("type");
    //TNE.debug("Type?: " + json.has("type"));
    //TNE.debug("Type: " + type);
    if(json.has("colour")) color = Color.fromRGB(json.getInteger("colour"));
    extended = json.getBoolean("extended");
    upgraded = json.getBoolean("upgraded");

    if(json.has("effects")) {
      JSONHelper effects = json.getHelper("effects");
      effects.getObject().forEach((key, value)->{
        final JSONHelper helperObj = new JSONHelper((JSONObject)value);
        customEffects.add(new PotionEffect(PotionEffectType.getByName(helperObj.getString("name")),
            helperObj.getInteger("amplifier"), helperObj.getInteger("duration"),
            helperObj.getBoolean("ambient"), helperObj.getBoolean("particles"),
            helperObj.getBoolean("icon")));
      });
    }
    //TNE.debug("Potion Data Reading End");
  }
}