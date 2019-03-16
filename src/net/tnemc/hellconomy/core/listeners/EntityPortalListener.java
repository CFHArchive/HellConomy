package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class EntityPortalListener implements Listener {

  private HellConomy plugin;

  public EntityPortalListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onNetherPortal(EntityPortalEvent event) {
    if(event.getEntityType().equals(EntityType.DROPPED_ITEM)) {
      final String world = HellConomy.instance().getWorldManager(event.getEntity().getWorld().getName()).getBalanceWorld();
      if(HellConomy.currencyManager().currencyFromItem(world, ((Item)event.getEntity()).getItemStack()).isPresent()) event.setCancelled(true);
    }
  }
}
