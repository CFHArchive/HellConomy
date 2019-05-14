package net.tnemc.hellconomy.core.api;

import net.tnemc.core.economy.currency.Tier;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.common.account.Balance;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import net.tnemc.hellconomy.core.common.account.IDStorage;
import net.tnemc.hellconomy.core.currency.CurrencyFormatter;
import net.tnemc.hellconomy.core.currency.HellCurrency;
import net.tnemc.hellconomy.core.currency.HellTier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
public class HellAPI {

  private HellConomy plugin;

  public HellAPI(HellConomy plugin) {
    this.plugin = plugin;
  }

  /**
   * @return Whether or not this implementation has bank support.
   */
  public boolean hasBanks() {
    return false;
  }

  /**
   * Checks to see if a {@link HellCurrency} exists with this name.
   *
   * @param name The name of the {@link HellCurrency} to search for.
   * @return True if the currency exists, else false.
   */
  public boolean hasCurrency(String name) {
    return hasCurrency(name, plugin.getDefaultWorld());
  }

  /**
   * Checks to see if a {@link HellCurrency} exists with this name.
   * @param name The name of the {@link HellCurrency} to search for.
   * @param world The name of the {@link World} to check for this {@link HellCurrency} in.
   * @return True if the currency exists, else false.
   */
  public boolean hasCurrency(String name, String world) {
    return HellConomy.currencyManager().contains(world, name);
  }

  /**
   * Finds the default {@link HellCurrency} for the server.
   * @return The default {@link HellCurrency} for the server.
   */
  public HellCurrency getDefault() {
    return HellConomy.currencyManager().get(plugin.getDefaultWorld());
  }

  /**
   * Finds the default {@link HellCurrency} for a {@link World}
   * @param world The name of the {@link World} to use.
   * @return The default {@link HellCurrency} for this {@link World}.
   */
  public HellCurrency getDefault(String world) {
    return HellConomy.currencyManager().get(world);
  }

  /**
   * Grabs a {@link Set} of {@link HellCurrency} objects that exist.
   * @return A Set containing all the {@link HellCurrency} objects that exist on this server.
   */
  public Set<HellCurrency> getCurrencies() {
    return new HashSet<>(HellConomy.currencyManager().getCurrencies());
  }

  /**
   * Grabs a {@link Set} of {@link HellCurrency} objects that exist in a {@link World}
   * @param world The name of the {@link World} to use in this search.
   * @return A Set containing all the {@link HellCurrency} objects that exist on this {@link World}.
   */
  public Set<HellCurrency> getCurrencies(String world) {
    return new HashSet<>(HellConomy.currencyManager().getWorldCurrencies(world));
  }

  /**
   * Checks to see if a {@link HellCurrency} has the specified tier.
   * @param name The name of the {@link HellTier} to search for.
   * @param currency The {@link HellCurrency} to search
   * @return True if the tier exists, otherwise false.
   */
  public boolean hasTier(String name, HellCurrency currency) {
    return currency.hasTier(name);
  }

  /**
   * Checks to see if a {@link HellCurrency} has the specified tier.
   * @param name The name of the {@link HellTier} to search for.
   * @param currency The {@link HellCurrency} to search
   * @param world The name of the {@link World} to use for search purposes.
   * @return True if the tier exists, otherwise false.
   */
  public boolean hasTier(String name, HellCurrency currency, String world) {
    return currency.hasTier(name);
  }

  /**
   * Returns a {@link Set} of {@link HellTier} objects associated with the specified {@link HellCurrency}.
   * @param currency The {@link HellCurrency} to grab the tiers from.
   * @return A Set containing all the {@link HellTier} objects belonging to this {@link HellCurrency}.
   */
  public Set<Tier> getTiers(HellCurrency currency) {
    return currency.getTiers();
  }

  /**
   * Attempts to retrieve an account by the specified identifier. This method should be used for non-player accounts.
   * @param identifier The of the account.
   * @return The instance of the account if it exists, otherwise null.
   */
  public HellAccount getAccount(String identifier) {
    return HellAccount.getAccount(getID(identifier));
  }

  /**
   * Attempts to retrieve an account by the specified identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return The instance of the account if it exists, otherwise null.
   */
  public HellAccount getAccount(UUID identifier) {
    return HellAccount.getAccount(identifier);
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account exists for this player, else false.
   */
  public boolean hasAccount(String identifier) {
    return HellAccount.exists(getID(identifier));
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account exists for this player, else false.
   */
  public boolean hasAccount(UUID identifier) {
    return HellAccount.exists(identifier);
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account was created, else false.
   */
  public boolean createAccount(String identifier) {
    return HellAccount.add(getID(identifier), getUsername(identifier), new Date().getTime(), isPlayer(identifier));
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account was created, else false.
   */
  public boolean createAccount(UUID identifier) {
    return HellAccount.add(identifier, getUsername(identifier.toString()), new Date().getTime(), isPlayer(identifier.toString()));
  }

  /**
   * This is a shortcut method that combines getAccount with createAccount. This method should be used for non-player
   * Accounts.
   * @param identifier The of the account.
   * @return The instance of the account.
   */
  public HellAccount getOrCreate(String identifier) {
    final UUID id = getID(identifier);
    if(!hasAccount(id)) createAccount(id);
    return getAccount(id);
  }

  /**
   * This is a shortcut method that combines getAccount with createAccount. This method should be used for non-player
   * Accounts.
   * @param identifier The {@link UUID} of the account.
   * @return The instance of the account.
   */
  public HellAccount getOrCreate(UUID identifier) {
    if(!hasAccount(identifier)) createAccount(identifier);
    return getAccount(identifier);
  }

  /**
   * Formats a monetary amount into a more text-friendly version.
   * @param amount The amount of currency to format.
   * @param world The {@link World} in which this format operation is occurring.
   * @return The formatted amount.
   */
  public String format(BigDecimal amount, String world) {
    return CurrencyFormatter.format(world, amount);
  }

  /**
   * Formats a monetary amount into a more text-friendly version.
   * @param amount The amount of currency to format.
   * @param currency The {@link HellCurrency} associated with the amount to be formatted.
   * @param world The {@link World} in which this format operation is occuring.
   * @return The formatted amount.
   */
  public String format(BigDecimal amount, HellCurrency currency, String world) {
    return CurrencyFormatter.format(world, currency.name(), amount);
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @return The balance of the account.
   */
  public BigDecimal getHoldings(String identifier) {
    getOrCreate(identifier);
    return Balance.getBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name());
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param world The name of the {@link World} associated with the balance.
   * @return The balance of the account.
   */
  public BigDecimal getHoldings(String identifier, String world) {
    getOrCreate(identifier);
    return Balance.getBalanceValue(getID(identifier), HellConomy.getServerName(), world, HellConomy.currencyManager().get(world).name());
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param world The name of the {@link World} associated with the balance.
   * @param currency The {@link HellCurrency} object associated with the balance.
   * @return The balance of the account.
   */
  public BigDecimal getHoldings(String identifier, String world, HellCurrency currency) {
    getOrCreate(identifier);
    return Balance.getBalanceValue(getID(identifier), HellConomy.getServerName(), world, currency.name());
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param currency The {@link HellCurrency} object associated with the balance.
   * @return The balance of the account for the specified {@link HellCurrency}.
   */
  public BigDecimal getHoldings(String identifier, HellCurrency currency) {
    getOrCreate(identifier);
    return Balance.getBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), currency.name());
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to use for this check.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  public boolean hasHoldings(String identifier, BigDecimal amount) {
    getOrCreate(identifier);
    return Balance.hasBalance(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name(), amount);
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to use for this check.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  public boolean hasHoldings(String identifier, BigDecimal amount, String world) {
    getOrCreate(identifier);
    return Balance.hasBalance(getID(identifier), HellConomy.getServerName(), world, HellConomy.currencyManager().get(world).name(), amount);
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to use for this check.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  public boolean hasHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    getOrCreate(identifier);
    return Balance.hasBalance(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), currency.name(), amount);
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to use for this check.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  public boolean hasHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    getOrCreate(identifier);
    return Balance.hasBalance(getID(identifier), HellConomy.getServerName(), world, currency.name(), amount);
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to add to this account.
   * @return True if the funds were added to the account, otherwise false.
   */
  public boolean addHoldings(String identifier, BigDecimal amount) {
    getOrCreate(identifier);
    Balance.addBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name(), amount);
    return true;
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  public boolean addHoldings(String identifier, BigDecimal amount, String world) {
    getOrCreate(identifier);
    Balance.addBalanceValue(getID(identifier), HellConomy.getServerName(), world, HellConomy.currencyManager().get(world).name(), amount);
    return true;
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to add to this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  public boolean addHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    getOrCreate(identifier);
    Balance.addBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), currency.name(), amount);
    return true;
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to add to this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  public boolean addHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    getOrCreate(identifier);
    Balance.addBalanceValue(getID(identifier), HellConomy.getServerName(), world, currency.name(), amount);
    return true;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to add to this account.
   * @return True if a call to the corresponding addHoldings method would return true, otherwise false.
   */
  public boolean canAddHoldings(String identifier, BigDecimal amount) {
    return true;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to add to this account.
   * @param world      The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding addHoldings method would return true, otherwise false.
   */
  public boolean canAddHoldings(String identifier, BigDecimal amount, String world) {
    return true;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to add to this account.
   * @param currency   The {@link HellCurrency} object associated with the amount.
   * @return True if a call to the corresponding addHoldings method would return true, otherwise false.
   */
  public boolean canAddHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    return true;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to add to this account.
   * @param currency   The {@link HellCurrency} object associated with the amount.
   * @param world      The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding addHoldings method would return true, otherwise false.
   */
  public boolean canAddHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    return true;
  }

  /**
   * Used to set funds for an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to set from this account.
   * @return True if the funds were changed for the account, otherwise false.
   */
  public boolean setHoldings(String identifier, BigDecimal amount) {
    Balance.setBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name(), amount);
    return true;
  }

  /**
   * Used to set funds for an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were changed for the account, otherwise false.
   */
  public boolean setHoldings(String identifier, BigDecimal amount, String world) {
    Balance.setBalanceValue(getID(identifier), HellConomy.getServerName(), world, HellConomy.currencyManager().get(world).name(), amount);
    return true;
  }

  /**
   * Used to set funds for an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to set from this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @return True if the funds were changed for the account, otherwise false.
   */
  public boolean setHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    Balance.setBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), currency.name(), amount);
    return true;
  }

  /**
   * Used to set funds for an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to set from this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were changed for the account, otherwise false.
   */
  public boolean setHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    Balance.setBalanceValue(getID(identifier), HellConomy.getServerName(), world, currency.name(), amount);
    return true;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were removed from the account, otherwise false.
   */
  public boolean removeHoldings(String identifier, BigDecimal amount) {
    getOrCreate(identifier);
    if(hasHoldings(identifier, amount)) {
      Balance.subBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name(), amount);
      return true;
    }
    return false;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  public boolean removeHoldings(String identifier, BigDecimal amount, String world) {
    getOrCreate(identifier);
    if(hasHoldings(identifier, amount, world)) {
      Balance.subBalanceValue(getID(identifier), HellConomy.getServerName(), world, HellConomy.currencyManager().get(world).name(), amount);
      return true;
    }
    return false;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to remove from this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  public boolean removeHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    getOrCreate(identifier);
    if(hasHoldings(identifier, amount, currency)) {
      Balance.subBalanceValue(getID(identifier), HellConomy.getServerName(), HellConomy.instance().getDefaultWorld(), currency.name(), amount);
      return true;
    }
    return false;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount The amount you wish to remove from this account.
   * @param currency The {@link HellCurrency} object associated with the amount.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  public boolean removeHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    getOrCreate(identifier);
    if(hasHoldings(identifier, amount, currency, world)) {
      Balance.subBalanceValue(getID(identifier), HellConomy.getServerName(), world, currency.name(), amount);
      return true;
    }
    return false;
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to remove from this account.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  public boolean canRemoveHoldings(String identifier, BigDecimal amount) {
    String world = plugin.getDefaultWorld();
    HellCurrency currency = HellConomy.currencyManager().get(world);
    return hasCurrency(currency.name(), world) && hasHoldings(identifier, amount, currency, world);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to remove from this account.
   * @param world      The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  public boolean canRemoveHoldings(String identifier, BigDecimal amount, String world) {
    HellCurrency currency = HellConomy.currencyManager().get(world);
    return hasCurrency(currency.name(), world) && hasHoldings(identifier, amount, currency, world);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to remove from this account.
   * @param currency   The {@link HellCurrency} object associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  public boolean canRemoveHoldings(String identifier, BigDecimal amount, HellCurrency currency) {
    return hasCurrency(currency.name()) && hasHoldings(identifier, amount, currency);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   *
   * @param identifier The identifier of the account. This may be a {@link UUID}, or a player's name.
   * @param amount     The amount you wish to remove from this account.
   * @param currency   The {@link HellCurrency} object associated with the amount.
   * @param world      The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  public boolean canRemoveHoldings(String identifier, BigDecimal amount, HellCurrency currency, String world) {
    return hasCurrency(currency.name(), world) && hasHoldings(identifier, amount, currency, world);
  }

  /**
   * @return Whether or not this implementation supports multiple-players sharing a bank account.
   */
  public boolean sharedBanks() {
    return false;
  }

  /**
   * Register a {@link HellCurrency}  to be used by other plugins.
   *
   * @param currency The {@link HellCurrency} to register.
   * @return True if the {@link HellCurrency} was registered, otherwise false.
   */
  public boolean registerCurrency(HellCurrency currency) {
    return registerCurrency(currency, plugin.getDefaultWorld());
  }

  /**
   * Register a {@link HellCurrency}  to be used by other plugins.
   *
   * @param currency The {@link HellCurrency} to register.
   * @param world    The name of the {@link World} to use during the registration process.
   * @return True if the {@link HellCurrency}  was registered, otherwise false.
   */
  public boolean registerCurrency(HellCurrency currency, String world) {
    HellConomy.currencyManager().addCurrency(world, currency);
    return true;
  }

  /**
   * Register a {@link HellCurrency} {@link HellTier} to be used by other plugins.
   *
   * @param tier     The {@link HellTier} to register.
   * @param currency The {@link HellCurrency} to register this {@link HellTier} under.
   * @return True if the {@link HellTier} was registered, otherwise false.
   */
  public boolean registerTier(HellTier tier, HellCurrency currency) {
    return registerTier(tier, currency, plugin.getDefaultWorld());
  }

  /**
   * Register a {@link HellCurrency} {@link HellTier} to be used by other plugins.
   *
   * @param tier     The {@link HellTier} to register.
   * @param currency The {@link HellCurrency} to register this {@link HellTier} under.
   * @param world    The name of the {@link World} to use during the registration process.
   * @return True if the {@link HellTier} was registered, otherwise false.
   */
  public boolean registerTier(HellTier tier, HellCurrency currency, String world) {
    if(HellConomy.currencyManager().contains(world, currency.name())) {
      if(tier.isMajor()) {
        HellConomy.currencyManager().get(world, currency.name()).addTNEMajorTier(tier);
      } else {
        HellConomy.currencyManager().get(world, currency.name()).addTNEMinorTier(tier);
      }
      return true;
    }
    return false;
  }

  public static UUID getID(String identifier) {
    if(isUUID(identifier)) return UUID.fromString(identifier);

    final OfflinePlayer player = Bukkit.getPlayer(identifier);
    if(player != null) {
      //System.out.println("ID: " + player.getUniqueId().toString());
      //System.out.println("Has Account?: " + HellAccount.exists(player.getUniqueId()));
      return player.getUniqueId();
    }

    UUID id = null;
    if(IDStorage.exists(identifier)) {
      id = IDStorage.getID(identifier);
    }

    if(id == null) {
      id = IDStorage.freeID();
      //System.out.println("Adding UUID for identifier: " + identifier);
      IDStorage.add(id, identifier);
    }

    return id;
  }

  public static String getUsername(String identifier) {
    //System.out.println("getUsername");
    if(isUUID(identifier)) {
      final UUID id = getID(identifier);
      final OfflinePlayer player = Bukkit.getOfflinePlayer(id);
      //System.out.println("checking player status");
      if(player != null && player.getName() != null) {
        //System.out.println("returning player name");
        return player.getName();
      }
      //System.out.println("returning from IDStorage");
      return IDStorage.getStorage(id).getString("display");
    }
    return identifier;
  }

  public static boolean isPlayer(String identifier) {
    if(identifier.contains("town")) {
      return false;
    } else if(identifier.contains("nation")) {
      return false;
    } else if(identifier.contains("faction")) {
      return false;
    } else if(identifier.contains("village")) {
      return false;
    } else if(identifier.contains("kingdom")) {
      return false;
    }
    final UUID id = getID(identifier);
    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
    return offlinePlayer != null;
  }

  public static boolean isUUID(String identifier) {
    try {
      UUID.fromString(identifier);
      return true;
    } catch(Exception ignore) {
      return false;
    }
  }
}