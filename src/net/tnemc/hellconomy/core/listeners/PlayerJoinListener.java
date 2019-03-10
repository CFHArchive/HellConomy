package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
public class PlayerJoinListener implements Listener {

  private HellConomy plugin;

  public PlayerJoinListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final UUID id = player.getUniqueId();
    final String world = HellConomy.worldManager().normalizeWorld(player.getWorld().getName());
  }
}