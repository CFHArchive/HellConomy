package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
public class PlayerQuitListener implements Listener {

  private HellConomy plugin;

  public PlayerQuitListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(final PlayerQuitEvent event) {
    final UUID id = event.getPlayer().getUniqueId();
    final Player player = event.getPlayer();
    Bukkit.getScheduler().runTaskAsynchronously(HellConomy.instance(), ()->{
      HellConomy.instance().saveManager().open();
      if(HellAccount.exists(id)) {
        HellAccount.saveItemCurrency(id, HellConomy.instance().normalizeWorld(player.getWorld().getName()), player.getInventory(), true);
      }
      HellConomy.instance().saveManager().close();
    });
  }
}