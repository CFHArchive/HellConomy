package net.tnemc.hellconomy.core.world;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class WorldEntry {

  private String name;
  private String balanceWorld;

  public WorldEntry(String name, String balanceWorld) {
    this.name = name;
    this.balanceWorld = balanceWorld;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBalanceWorld() {
    return balanceWorld;
  }

  public void setBalanceWorld(String balanceWorld) {
    this.balanceWorld = balanceWorld;
  }
}