package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * HellConomy Minecraft Server Plugin
 * <p>
 * Created by Daniel on 5/28/2018.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class GemsEconomy extends Converter {
  private File configFile = new File("plugins/GemsEconomy/config.yml");
  private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
  @Override
  public String name() {
    return "GemsEconomy";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);
    String table = config.getString("mysql.tableprefix") + "_balances";

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT * FROM " + table + ";")) {

      while(results.next()) {
        Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
        if(HellConomy.currencyManager().contains(HellConomy.instance().getDefaultWorld(), results.getString("currency_id"))) {
          currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld(), results.getString("currency_id"));
        }
        Converter.convertedAdd(results.getString("account_id"),
            HellConomy.instance().getDefaultWorld(), currency.name(),
            new BigDecimal(results.getDouble("balance")));
      }
    } catch(SQLException ignore) {}
    db.close();
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {
    File dataFile = new File("plugins/GemsEconomy/data.yml");
    FileConfiguration dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);

    final ConfigurationSection accountSection = dataConfiguration.getConfigurationSection("accounts");
    if(accountSection != null) {
      final Set<String> accounts = accountSection.getKeys(false);
      for(String uuid : accounts) {
        final ConfigurationSection balanceSection = accountSection.getConfigurationSection(uuid + ".balances");
        if(balanceSection != null) {
          final Set<String> currencies = balanceSection.getKeys(false);
          for(String currency : currencies) {
            Currency cur = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
            if(HellConomy.currencyManager().contains(HellConomy.instance().getDefaultWorld(), currency)) {
              cur = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld(), currency);
            }
            Converter.convertedAdd(HellAPI.getUsername(uuid), HellConomy.instance().getDefaultWorld(), cur.name(), new BigDecimal(config.getString("accounts." + uuid + ".balances." + currency)));
          }
        }
      }
    }
  }
}
