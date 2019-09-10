package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.math.BigDecimal;
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
public class DeathListener implements Listener {

  private HellConomy plugin;

  public DeathListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final UUID id = player.getUniqueId();
    HellConomy.instance().saveManager().open();
    final String world = HellConomy.instance().normalizeWorld(player.getWorld().getName());

    HellConomy.instance().getWorldManager(world).getCurrencies().forEach(value -> {
      HellConomy.api().setHoldings(id.toString(), BigDecimal.ZERO, value, world);
    });
    HellConomy.instance().saveManager().close();
  }
}