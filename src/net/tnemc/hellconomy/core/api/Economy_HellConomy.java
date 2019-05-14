package net.tnemc.hellconomy.core.api;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tnemc.hellconomy.core.HellConomy;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class Economy_HellConomy implements Economy {

  private HellConomy plugin = null;
  private HellAPI api = null;

  public Economy_HellConomy(HellConomy plugin) {
    this.plugin = plugin;
    this.api = plugin.api();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "HellConomy";
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  @Override
  public int fractionalDigits() {
    return 2;
  }

  @Override
  public String format(double amount) {
    return api.format(new BigDecimal(amount + ""), plugin.getDefaultWorld());
  }

  @Override
  public String currencyNamePlural() {
    return HellConomy.currencyManager().get(plugin.getDefaultWorld()).plural();
  }

  @Override
  public String currencyNameSingular() {
    return HellConomy.currencyManager().get(plugin.getDefaultWorld()).name();
  }

  @Override
  public boolean hasAccount(String username) {
    //TNE.debug("Economy_TheNewEconomy.hasAccount:" + username + " Has?" + api.hasAccount(username));
    HellConomy.instance().saveManager().open();
    final boolean exists = api.hasAccount(username);
    HellConomy.instance().saveManager().close();
    return exists;
  }

  @Override
  public boolean hasAccount(OfflinePlayer offlinePlayer) {
    HellConomy.instance().saveManager().open();
    final boolean exists = api.hasAccount(offlinePlayer.getUniqueId().toString());
    HellConomy.instance().saveManager().close();
    return exists;
  }

  @Override
  public boolean hasAccount(String username, String world) {
    HellConomy.instance().saveManager().open();
    final boolean exists = api.hasAccount(username);
    HellConomy.instance().saveManager().close();
    return exists;
  }

  @Override
  public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
    HellConomy.instance().saveManager().open();
    final boolean exists = api.hasAccount(offlinePlayer.getUniqueId().toString());
    HellConomy.instance().saveManager().close();
    return exists;
  }

  @Override
  public double getBalance(String username) {
    return getBalance(username, plugin.getDefaultWorld());
  }

  @Override
  public double getBalance(OfflinePlayer offlinePlayer) {
    return getBalance(offlinePlayer.getUniqueId().toString(), plugin.getDefaultWorld());
  }

  @Override
  public double getBalance(String username, String world) {
    HellConomy.instance().saveManager().open();
    final double balance = api.getHoldings(username, world).doubleValue();
    HellConomy.instance().saveManager().close();
    return balance;
  }

  @Override
  public double getBalance(OfflinePlayer offlinePlayer, String world) {
    return getBalance(offlinePlayer.getUniqueId().toString(), world);
  }

  @Override
  public boolean has(String username, double amount) {
    //TNE.debug("Economy_TheNewEconomy.has(username, amount)");
    //TNE.debug("Amount: " + amount);
    return has(username, plugin.getDefaultWorld(), amount);
  }

  @Override
  public boolean has(OfflinePlayer offlinePlayer, double amount) {
    return has(offlinePlayer.getUniqueId().toString(), plugin.getDefaultWorld(), amount);
  }

  @Override
  public boolean has(String username, String world, double amount) {
    //TNE.debug("Economy_TheNewEconomy.has(username, world, amount)");
    //TNE.debug("Amount: " + amount);
    HellConomy.instance().saveManager().open();
    final boolean has = api.hasHoldings(username, new BigDecimal(amount + ""), world);
    HellConomy.instance().saveManager().close();
    return has;
  }

  @Override
  public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
    return has(offlinePlayer.getUniqueId().toString(), world, amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(String username, double amount) {
    return withdrawPlayer(username, plugin.getDefaultWorld(), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
    return withdrawPlayer(offlinePlayer.getUniqueId().toString(), plugin.getDefaultWorld(), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(String username, String world, double amount) {
    if(!hasAccount(username)) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
    }

    if(amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative amounts.");
    }

    if(!has(username, world, amount)) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds!");
    }

    HellConomy.instance().saveManager().open();
    final boolean remove = api.removeHoldings(username, new BigDecimal(amount + ""), world);
    HellConomy.instance().saveManager().close();
    if(remove) {

      return new EconomyResponse(amount, getBalance(username, world), EconomyResponse.ResponseType.SUCCESS, "");
    }
    return new EconomyResponse(amount, getBalance(username, world), EconomyResponse.ResponseType.FAILURE, "Unable to complete transaction!");
  }

  @Override
  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
    return withdrawPlayer(offlinePlayer.getUniqueId().toString(), world, amount);
  }

  @Override
  public EconomyResponse depositPlayer(String username, double amount) {
    return depositPlayer(username, plugin.getDefaultWorld(), amount);
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
    return depositPlayer(offlinePlayer.getUniqueId().toString(), plugin.getDefaultWorld(), amount);
  }

  @Override
  public EconomyResponse depositPlayer(String username, String world, double amount) {
    if(!hasAccount(username)) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
    }

    if(amount < 0) {
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative amounts.");
    }
    HellConomy.instance().saveManager().open();
    final boolean add = api.addHoldings(username, new BigDecimal(amount + ""), world);
    HellConomy.instance().saveManager().close();

    if(add) {
      return new EconomyResponse(amount, getBalance(username, world), EconomyResponse.ResponseType.SUCCESS, "");
    }
    return new EconomyResponse(amount, getBalance(username, world), EconomyResponse.ResponseType.FAILURE, "Unable to complete transaction!");
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
    return depositPlayer(offlinePlayer.getUniqueId().toString(), world, amount);
  }

  @Override
  public EconomyResponse createBank(String name, String world) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse createBank(String name, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse deleteBank(String name) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse bankBalance(String name) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse bankHas(String name, double amount) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse bankWithdraw(String name, double amount) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse bankDeposit(String name, double amount) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse isBankOwner(String name, String username) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse isBankOwner(String name, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse isBankMember(String name, String username) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public EconomyResponse isBankMember(String name, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported yet!");
  }

  @Override
  public List<String> getBanks() {
    return new ArrayList<>();
  }

  @Override
  public boolean createPlayerAccount(String username) {
    HellConomy.instance().saveManager().open();
    final boolean created = api.createAccount(username);
    HellConomy.instance().saveManager().close();
    return created;
  }

  @Override
  public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
    HellConomy.instance().saveManager().open();
    final boolean created = api.createAccount(offlinePlayer.getUniqueId().toString());
    HellConomy.instance().saveManager().close();
    return created;
  }

  @Override
  public boolean createPlayerAccount(String username, String world) {
    HellConomy.instance().saveManager().open();
    final boolean created = api.createAccount(username);
    HellConomy.instance().saveManager().close();
    return created;
  }

  @Override
  public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String world) {
    HellConomy.instance().saveManager().open();
    final boolean created = api.createAccount(offlinePlayer.getUniqueId().toString());
    HellConomy.instance().saveManager().close();
    return created;
  }
}