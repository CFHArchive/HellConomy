package net.tnemc.hellconomy.core.item.data;

import net.tnemc.hellconomy.core.item.JSONHelper;
import net.tnemc.hellconomy.core.item.SerialItemData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by Daniel on 11/10/2017.
 */
public class SkullData implements SerialItemData {

  private UUID owner;
  private boolean valid = false;

  @Override
  public SerialItemData initialize(ItemStack stack) {
    ItemMeta meta = stack.getItemMeta();
    if(meta instanceof SkullMeta) {
      valid = true;
      if(((SkullMeta) meta).hasOwner()) {
        owner = ((SkullMeta) meta).getOwningPlayer().getUniqueId();
      }
    }
    return this;
  }

  @Override
  public ItemStack build(ItemStack stack) {
    if(valid) {
      SkullMeta meta = (SkullMeta)stack.getItemMeta();
      if(owner != null) meta.setOwner(owner.toString());
      stack.setItemMeta(meta);
    }
    return stack;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("name", "skull");
    if(owner != null) json.put("owner", owner.toString());
    return json;
  }

  @Override
  public void readJSON(JSONHelper json) {
    valid = true;
    if(json.has("owner")) owner = UUID.fromString(json.getString("owner"));
  }
}
