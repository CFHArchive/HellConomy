package net.tnemc.hellconomy.core.common.account;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.currency.HellCurrency;
import net.tnemc.hellconomy.core.currency.ItemCalculations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.Table;

import java.math.BigDecimal;
import java.util.List;
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

@DbName("HellConomy")
@Table("hellco_accounts")
@CompositePK({"account_id"})
public class HellAccount extends Model {

  //account_id
  //account_display
  //account_created
  //account_player

  public static boolean add(UUID id, String display, long created, boolean player) {
    boolean exists = exists(id);
    HellAccount account = (exists)? getAccount(id) : new HellAccount();

    if(!exists) account.set("account_id", freeID().toString());
    account.set("account_display", display);
    if(!exists) account.set("account_created", created);
    if(!exists) account.set("account_player", player);

    return account.saveIt();
  }

  public static HellAccount getAccount(UUID id) {
    return HellAccount.findFirst("account_id = ?", id.toString());
  }

  public static UUID freeID() {
    UUID id = UUID.randomUUID();
    if(exists(id)) {
      //should never happen.
      while(exists(id)) {
        id = UUID.randomUUID();
      }
    }
    return id;
  }

  public static boolean exists(UUID id) {
    return HellAccount.findFirst("account_id = ?", id.toString()) != null;
  }

  public static void delete(final UUID id) {
    HellAccount.delete("account_id = ?", id.toString());
  }

  public static BigDecimal getHoldings(final UUID id, String world, HellCurrency currency, boolean database) {
    BigDecimal current = BigDecimal.ZERO;
    world = HellConomy.instance().getWorldManager(world).getBalanceWorld();
    final Player player = Bukkit.getPlayer(id);
    if(database || !currency.isItem() || player == null) {
      HellConomy.instance().saveManager().open();
      current = Balance.getBalanceValue(id, HellConomy.getServerName(), world, currency.name());
      HellConomy.instance().saveManager().close();
      if(current == null) current = BigDecimal.ZERO;
    } else {
      current = ItemCalculations.getCurrencyItems(currency, player.getInventory());
    }
    return current;
  }

  public static void initializeHoldings(final UUID id, String world) {
    HellConomy.currencyManager().getWorldCurrencies(world).forEach((currency)->{
      if(currency.defaultBalance().compareTo(BigDecimal.ZERO) > 0 &&!Balance.exists(id, HellConomy.getServerName(), world, currency.name())) {
        Balance.add(id, HellConomy.getServerName(), world, currency.name(), currency.defaultBalance());
      }
    });
  }

  public static void saveItemCurrency(final UUID id, final String world, final PlayerInventory playerInventory, boolean save) {
    List<String> currencies = HellConomy.instance().getWorldManager(world).getItemCurrencies();

    currencies.forEach((currency)->{
      final HellCurrency cur = HellConomy.currencyManager().get(world, currency);
      Balance.add(id, HellConomy.getServerName(), world, currency, ItemCalculations.getCurrencyItems(cur, playerInventory));
    });
  }
}