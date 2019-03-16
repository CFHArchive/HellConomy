package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class TeleportListener implements Listener {

  private HellConomy plugin;

  public TeleportListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onTeleport(final PlayerTeleportEvent event) {
    String fromWorld = event.getFrom().getWorld().getName();
    fromWorld = HellConomy.instance().getWorldManager(fromWorld).getBalanceWorld();
    String toWorld = event.getTo().getWorld().getName();
    toWorld = HellConomy.instance().getWorldManager(toWorld).getBalanceWorld();

    if(!fromWorld.equals(toWorld) && HellConomy.mapper().getBool("Core.Multiworld")) {
      final PlayerInventory inventory = event.getPlayer().getInventory();
      HellAccount.saveItemCurrency(event.getPlayer().getUniqueId(), fromWorld, inventory, true);
    }
  }
}