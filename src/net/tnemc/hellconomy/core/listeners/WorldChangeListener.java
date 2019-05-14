package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import net.tnemc.hellconomy.core.currency.HellCurrency;
import net.tnemc.hellconomy.core.currency.ItemCalculations;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.UUID;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class WorldChangeListener implements Listener {

  private HellConomy plugin;

  public WorldChangeListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onWorldChange(final PlayerChangedWorldEvent event) {
    final Player player = event.getPlayer();
    final UUID id = player.getUniqueId();
    final String world = HellConomy.instance().normalizeWorld(player.getWorld().getName());

    if(HellConomy.mapper().getBool("server.multi_world") &&
        !HellConomy.instance().getWorldManager(event.getFrom().getName()).getBalanceWorld().equalsIgnoreCase(world)) {
      HellConomy.instance().getWorldManager(world).getItemCurrencies().forEach(value -> {
        final HellCurrency currency = HellConomy.currencyManager().get(world, value);
        ItemCalculations.setItems(HellConomy.currencyManager().get(world, value),
                                  HellAccount.getHoldings(id, world, currency, true), player.getInventory(), false);
      });
    }
  }
}