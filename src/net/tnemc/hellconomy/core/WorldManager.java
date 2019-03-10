package net.tnemc.hellconomy.core;

import net.tnemc.hellconomy.core.world.WorldEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class WorldManager {

  private Map<String, WorldEntry> worlds = new HashMap<>();

  public void addEntry(WorldEntry entry) {
    worlds.put(entry.getName(), entry);
  }

  public void removeEntry(String name) {
    worlds.remove(name);
  }

  public WorldEntry getEntry(String name) {
    return worlds.get(name);
  }

  public String normalizeWorld(String world) {
    if(!HellConomy.mapper().getBool("server.multi_world")) {
      return HellConomy.instance().getDefaultWorld();
    }

    if(worlds.containsKey(world)) return getEntry(world).getBalanceWorld();
    return world;
  }
}