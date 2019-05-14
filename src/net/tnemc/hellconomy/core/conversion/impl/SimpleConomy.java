package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

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
public class SimpleConomy extends Converter {
  private File configFile = new File("plugins/SimpleConomy/config.yml");
  private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
  @Override
  public String name() {
    return "SimpleConomy";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);
    String table = config.getString("mySqlSettings.Connection.Values.table");

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT UUID, COINS FROM " + table + ";")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      HellConomy.instance().saveManager().open();
      while(results.next()) {
        Converter.convertedAdd(HellAPI.getUsername(results.getString("UUID")),
                               HellConomy.instance().getDefaultWorld(), currency.name(),
                               new BigDecimal(results.getDouble("COINS")));
      }
      HellConomy.instance().saveManager().close();
    } catch(SQLException ignore) {}
    db.close();
  }
}
