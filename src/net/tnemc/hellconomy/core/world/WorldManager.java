package net.tnemc.hellconomy.core.world;

import net.tnemc.hellconomy.core.currency.HellCurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
  private Map<String, HellCurrency> currencies = new HashMap<>();
  private List<String> itemCurrencies = new ArrayList<>();

  private String world;
  private String balanceWorld = null;

  public WorldManager(String world) {
    this.world = world;
    this.balanceWorld = world;
  }

  public WorldManager(String world, String balanceWorld) {
    this.world = world;
    this.balanceWorld = balanceWorld;
  }

  public void addCurrency(HellCurrency currency) {
    if(currency.isItem()) {
      itemCurrencies.add(currency.name());
    }
    currencies.put(currency.name(), currency);
  }

  public HellCurrency getCurrency(String currency) {
    return currencies.get(currency);
  }

  public void removeCurrency(String currency) {
    currencies.remove(currency);
  }

  public Collection<HellCurrency> getCurrencies() {
    return currencies.values();
  }

  public List<String> getItemCurrencies() {
    return itemCurrencies;
  }

  public boolean containsCurrency(String currency) {
    for(String s : currencies.keySet()) {
      if(s.equalsIgnoreCase(currency)) return true;
    }
    return false;
  }

  public String getWorld() {
    return world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  public String getBalanceWorld() {
    return balanceWorld;
  }

  public void setBalanceWorld(String balanceWorld) {
    this.balanceWorld = balanceWorld;
  }
}