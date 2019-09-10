package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
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
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class Essentials extends Converter {
  private File dataDirectory = new File(HellConomy.instance().getDataFolder(), "../Essentials/userdata");

  @Override
  public String name() {
    return "Essentials";
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {
    if(!dataDirectory.isDirectory() || dataDirectory.listFiles() == null || dataDirectory.listFiles().length == 0) return;

    for(File accountFile : dataDirectory.listFiles()) {
      FileConfiguration acc = YamlConfiguration.loadConfiguration(accountFile);

      final BigDecimal money = acc.contains("money")? new BigDecimal(acc.getString("money")) : BigDecimal.ZERO;
      String currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name();

      Converter.convertedAdd(acc.getString("lastAccountName"), HellConomy.instance().getDefaultWorld(), currency, money);
    }
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    File configFile = new File(HellConomy.instance().getDataFolder(), "../EssentialsMysqlStorage/config.yml");
    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    final String table = config.getString("Database.Mysql.TableName");
    db.open(dataSource);

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT player_uuid, money, offline_money FROM " + table + ";")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        Converter.convertedAdd(results.getString("player_uuid"),
                               HellConomy.instance().getDefaultWorld(), currency.name(),
                                      new BigDecimal(results.getDouble("money")));
        Converter.convertedAdd(results.getString("player_uuid"),
                               HellConomy.instance().getDefaultWorld(), currency.name(),
                                      new BigDecimal(results.getDouble("offline_money")));
      }
    } catch(SQLException ignore) {}
    db.close();
  }
}