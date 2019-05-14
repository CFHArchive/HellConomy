package net.tnemc.hellconomy.core;

import net.tnemc.config.CommentedConfiguration;
import net.tnemc.hellconomy.core.currency.HellCurrency;
import net.tnemc.hellconomy.core.currency.HellTier;
import net.tnemc.hellconomy.core.currency.ItemTier;
import net.tnemc.hellconomy.core.world.WorldManager;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class CurrencyManager {
  private static BigDecimal largestSupported;
  private Map<String, HellCurrency> globalCurrencies = new HashMap<>();

  //Cache-related maps.
  private List<String> globalDisabled = new ArrayList<>();

  public CurrencyManager() {
    loadCurrencies();
  }

  public void loadCurrencies() {
    largestSupported = new BigDecimal("900000000000000000000000000000000000000000000");

    loadBasic();
    for(WorldManager manager : HellConomy.instance().getWorldManagers()) {
      initializeWorld(manager.getWorld());
    }
    largestSupported = null;
  }

  private void loadBasic() {

    final String base = "currency";

    //Currency Info Configurations.
    final String server = HellConomy.mapper().getString("server.name", "Main Server");
    final String single = HellConomy.mapper().getString(base + ".major_single", "Dollar");
    final String plural = HellConomy.mapper().getString(base + ".major_plural", "Dollars");
    final String singleMinor = HellConomy.mapper().getString(base + ".minor_single", "Cent");
    final String pluralMinor = HellConomy.mapper().getString(base + ".minor_plural", "Cents");
    final String prefixes = HellConomy.mapper().getString(base + ".prefixes", "kMGTPEZYXWVUN₮").trim();
    final String symbol = HellConomy.mapper().getString(base + ".symbol", "$");
    final Boolean item = HellConomy.mapper().getBool(base + ".item_currency");

    //Currency Options Configurations.
    final String format = HellConomy.mapper().getString(base + ".format", "<symbol><major.amount><decimal><minor.amount>").trim();
    final BigDecimal maxBalance = ((new BigDecimal(HellConomy.mapper().getString("account.max_balance", largestSupported.toPlainString())).compareTo(largestSupported) > 0)? largestSupported : new BigDecimal(HellConomy.mapper().getString(base + ".MaxBalance", largestSupported.toPlainString())));
    final BigDecimal balance = new BigDecimal(HellConomy.mapper().getString("account.balance", "200.00"));
    final String decimal = ".";
    final Boolean ender = false;
    final Boolean separate = true;
    final String separator = ",";
    final Integer minorWeight = 100;

    //Currency Note Configurations
    final Boolean notable = false;
    final BigDecimal fee = new BigDecimal("0.00");
    final BigDecimal minimum = new BigDecimal("0.00");

    HellCurrency currency = new HellCurrency();
    currency.setServer(server);
    currency.setIdentifier(single);
    currency.setMaxBalance(maxBalance);
    currency.setBalance(balance);
    currency.setDecimal(decimal);
    currency.setDecimalPlaces(2);
    currency.setFormat(format);
    currency.setPrefixes(prefixes);
    currency.setSingle(single);
    currency.setPlural(plural);
    currency.setSingleMinor(singleMinor);
    currency.setPluralMinor(pluralMinor);
    currency.setSymbol(symbol);
    currency.setWorldDefault(true);
    currency.setRate(1.0);
    currency.setItem(item);
    currency.setXp(false);
    currency.setNotable(notable);
    currency.setFee(fee);
    currency.setMinimum(minimum);
    currency.setEnderChest(ender);
    currency.setSeparateMajor(separate);
    currency.setMajorSeparator(separator);
    currency.setMinorWeight(minorWeight);

    loadBasicTiers(currency, HellConomy.mapper().getConfiguration("currency"), item);

    addCurrency(HellConomy.instance().getDefaultWorld(), currency);
  }

  private void loadBasicTiers(HellCurrency currency, CommentedConfiguration configuration, boolean item) {
    final String baseNode = "currency." + ((item)? "items" : "virtual");
    Set<String> tiers = configuration.getSection(baseNode).getKeys(false);

    for (String tierName : tiers) {

      //Normal HellTier variables
      String unparsedValue = configuration.getString(baseNode + "." + tierName);

      final String type = (unparsedValue.contains("."))? "minor" : "major";

      if(type.equalsIgnoreCase("minor")) {
        unparsedValue = unparsedValue.split("\\.")[1];
      }

      ItemTier itemTier = null;

      if (item) {
        itemTier = new ItemTier(tierName, (short)0);
        itemTier.setName(null);
        itemTier.setLore(null);
      }

      HellTier tier = new HellTier();
      tier.setMajor(type.equalsIgnoreCase("major"));
      tier.setItemInfo(itemTier);
      tier.setSingle(tierName);
      tier.setPlural(tierName + "s");
      tier.setWeight(Integer.valueOf(unparsedValue));

      if (type.equalsIgnoreCase("minor")) {
        currency.addTNEMinorTier(tier);
        continue;
      }
      currency.addTNEMajorTier(tier);
    }
  }

  public void addCurrency(String world, HellCurrency currency) {
    //TNE.debug("[Add]Loading Currency: " + currency.name() + " for world: " + world + " with default balance of " + currency.defaultBalance());
    if(world.equalsIgnoreCase(HellConomy.instance().getDefaultWorld())) {
      globalCurrencies.put(currency.name(), currency);
    } else {
      WorldManager manager = HellConomy.instance().getWorldManager(world);
      if (manager != null) {
        //TNE.debug("[Add]Adding Currency: " + currency.name() + " for world: " + world);
        manager.addCurrency(currency);
      }
      HellConomy.instance().addWorldManager(manager);
    }
  }

  public void initializeWorld(String world) {
    //TNE.debug("Initializing World: " + world);
    if(!HellConomy.instance().getWorldManagersMap().containsKey(world)) {
      WorldManager manager = new WorldManager(world);
      HellConomy.instance().addWorldManager(manager);
    }
    WorldManager manager = HellConomy.instance().getWorldManager(world);
    for(HellCurrency currency : globalCurrencies.values()) {
      if(!globalDisabled.contains(currency.name())) {
        if(manager == null) {
          //TNE.debug("World Manager for world: " + world + " is null. Skipping.");
          break;
        }
        manager.addCurrency(currency);
      }
    }
    HellConomy.instance().addWorldManager(manager);
  }

  public HellCurrency get(String world) {
    for(HellCurrency currency : HellConomy.instance().getWorldManager(world).getCurrencies()) {
      //TNE.debug("Currency: " + currency.name() + " World: " + world + " Default? " + currency.isDefault());
      if(currency.isDefault()) {
        //TNE.debug("Returning default Currency of " + currency.name() + " for world " + world);
        return currency;
      }
    }
    return null;
  }

  public HellCurrency get(String world, String name) {
    ////TNE.debug("Currency: " + name);
    ////TNE.debug("World: " + world);
    ////TNE.debug("WorldManager null for " + world +"? " + (HellConomy.instance().getWorldManager(world) == null));
    if(HellConomy.instance().getWorldManager(world).containsCurrency(name)) {
      ////TNE.debug("Returning Currency " + name + " for world " + world);
      return HellConomy.instance().getWorldManager(world).getCurrency(name);
    }
    return get(world);
  }

  public BigDecimal convert(HellCurrency from, HellCurrency to, BigDecimal amount) {
    double fromRate = from.getRate();
    double toRate = to.getRate();

    return convert(fromRate, toRate, amount);
  }

  public BigDecimal convert(HellCurrency from, double toRate, BigDecimal amount) {
    return convert(from.getRate(), toRate, amount);
  }

  public BigDecimal convert(double fromRate, double toRate, BigDecimal amount) {
    double rate = fromRate - toRate;
    BigDecimal difference = amount.multiply(new BigDecimal(rate + ""));

    return amount.add(difference);
  }

  public boolean contains(String world) {
    return HellConomy.instance().getWorldManager(world) != null;
  }

  public Collection<HellCurrency> getWorldCurrencies(String world) {
    //TNE.debug("=====START CurrencyManager =====");
    //System.out.println("World: " + world);
    //System.out.println("Worlds: " + world);
    world = HellConomy.instance().getWorldManager(world).getBalanceWorld();
    return HellConomy.instance().getWorldManager(world).getCurrencies();
  }

  public Collection<HellCurrency> getCurrencies() {
    List<HellCurrency> currencies = new ArrayList<>();
    HellConomy.instance().getWorldManagers().forEach((worldManager)->{
      currencies.addAll(worldManager.getCurrencies());
    });

    return currencies;
  }

  public void register(net.tnemc.core.economy.currency.Currency currency) {
    addCurrency(HellConomy.instance().getDefaultWorld(), HellCurrency.fromReserve(currency));
  }

  public Optional<HellCurrency> currencyFromItem(String world, ItemStack stack) {
    ItemStack clone = stack;
    clone.setAmount(1);
    for(HellCurrency currency : HellConomy.instance().getWorldManager(world).getCurrencies()) {
      if(currency.isItem() && (isMajorItem(world, currency.name(), clone) ||
          isMinorItem(world, currency.name(), clone))) {
        return Optional.of(currency);
      }
    }
    return Optional.empty();
  }

  public boolean isMajorItem(String world, String currency, ItemStack stack) {
    for(Object tier : HellConomy.instance().getWorldManager(world).getCurrency(currency).getTNEMajorTiers().values()) {
      if((tier instanceof HellTier) && ((HellTier)tier).getItemInfo().toStack().equals(stack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isMinorItem(String world, String currency, ItemStack stack) {
    for(Object tier : HellConomy.instance().getWorldManager(world).getCurrency(currency).getTNEMinorTiers().values()) {
      if((tier instanceof HellTier) && ((HellTier)tier).getItemInfo().toStack().equals(stack)) {
        return true;
      }
    }
    return false;
  }

  public boolean contains(String world, String name) {
    //TNE.debug("CurrencyManager.contains(" + world + ", " + name + ")");
    return HellConomy.instance().getWorldManager(world).containsCurrency(name);
  }
}