package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Date;
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
public class ConnectionListener implements Listener {

  private HellConomy plugin;

  public ConnectionListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onConnect(AsyncPlayerPreLoginEvent event) {
    final UUID id = event.getUniqueId();
    final String name = event.getName();
    HellConomy.instance().saveManager().open();
    if(!HellAccount.exists(id)) {
      HellAccount.add(id, name, new Date().getTime(), true);
    }
    HellConomy.instance().saveManager().close();
  }
}