package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import net.tnemc.hellconomy.core.currency.HellCurrency;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class MineConomy extends Converter {
  private File accountsFile = new File(HellConomy.instance().getDataFolder(), "../MineConomy/accounts.yml");
  private File banksFile = new File(HellConomy.instance().getDataFolder(), "../MineConomy/banks.yml");
  private File currencyFile = new File(HellConomy.instance().getDataFolder(), "../MineConomy/currencies.yml");
  private FileConfiguration accounts = YamlConfiguration.loadConfiguration(accountsFile);
  private FileConfiguration banks = YamlConfiguration.loadConfiguration(banksFile);
  private FileConfiguration currencies = YamlConfiguration.loadConfiguration(currencyFile);

  @Override
  public String name() {
    return "MineConomy";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);

    String table = "mineconomy_accounts";

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT * FROM " + table + ";")) {

      HellCurrency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        String username = results.getString("account");
        Double balance = results.getDouble("balance");
        String currencyName = results.getString("currency");

        String currencyPath = "Currencies." + currencyName + ".Value";
        double rate = (currencies.contains(currencyPath))? currencies.getDouble(currencyPath) : 1.0;
        if(rate > 1.0) rate = 1.0;
        else if(rate < 0.1) rate = 0.1;

        if(HellConomy.currencyManager().contains(HellConomy.instance().getDefaultWorld(), currencyName)) {
          currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld(), currencyName);
        }
        Converter.convertedAdd(username, HellConomy.instance().getDefaultWorld(), currency.name(),
            HellConomy.currencyManager().convert(rate, currency.getRate(), new BigDecimal(balance)));
      }
    } catch(SQLException ignore) {}
    db.close();
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {

    String base = "Accounts";
    HellCurrency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
    for(String username : accounts.getConfigurationSection(base).getKeys(false)) {

      double amount = accounts.getDouble(base + "." + username + ".Balance");
      String currencyPath = (currencies != null)? "Currencies." + accounts.getString(base + "." + username + ".Currency") + ".Value" : null;
      double rate = (currencyPath != null && currencies != null && currencies.contains(currencyPath))? currencies.getDouble(currencyPath) : 1.0;
      if(rate > 1.0) rate = 1.0;
      else if(rate < 0.1) rate = 0.1;

      if(HellConomy.currencyManager().contains(HellConomy.instance().getDefaultWorld(), accounts.getString(base + "." + username + ".Currency"))) {
        currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld(), accounts.getString(base + "." + username + ".Currency"));
      }
      Converter.convertedAdd(username, HellConomy.instance().getDefaultWorld(), currency.name(), HellConomy.currencyManager().convert(rate, currency.getRate(), new BigDecimal(amount)));
    }

    base = "Banks";

    if(banks != null && banks.contains(base) && !banks.isString(base)) {
      for (String bank : banks.getConfigurationSection(base).getKeys(false)) {
        base = "Banks." + bank + ".Accounts";
        if(banks.contains(base)) {
          for (String username : banks.getConfigurationSection(base).getKeys(false)) {
            Converter.convertedAdd(username, HellConomy.instance().getDefaultWorld(), currency.name(), new BigDecimal(banks.getDouble(base + "." + username + ".Balance")));
          }
        }
      }
    }
  }
}
