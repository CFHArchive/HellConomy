package net.tnemc.hellconomy.core.listeners;

import net.tnemc.hellconomy.core.HellConomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class WorldLoadListener implements Listener {

  private HellConomy plugin;

  public WorldLoadListener(HellConomy plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onWorldLoad(final WorldLoadEvent event) {
    String world = event.getWorld().getName();
    System.out.println("WorldLoadEvent Start " + world);
    HellConomy.currencyManager().initializeWorld(world);
    System.out.println("WorldLoadEvent End " + world);
  }
}