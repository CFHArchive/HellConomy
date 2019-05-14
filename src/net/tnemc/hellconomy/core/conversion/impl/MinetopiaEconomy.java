package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
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
public class MinetopiaEconomy extends Converter {
  @Override
  public String name() {
    return "MinetopiaEconomy";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT username, money FROM UserData;")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        Converter.convertedAdd(results.getString("username"),
            HellConomy.instance().getDefaultWorld(), currency.name(),
            new BigDecimal(results.getDouble("money")));
      }
    } catch(SQLException ignore) {}
    db.close();
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {
    File configFile = new File("plugins/MinetopiaEconomy/data/userdata.yml");
    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    final ConfigurationSection accountSection = config.getConfigurationSection("players");
    if(accountSection != null) {
      final Set<String> accounts = accountSection.getKeys(false);
      for(String uuid : accounts) {
        Currency cur = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
        Converter.convertedAdd(uuid, HellConomy.instance().getDefaultWorld(), cur.name(), new BigDecimal(config.getString("players." + uuid)));
      }
    }
  }
}