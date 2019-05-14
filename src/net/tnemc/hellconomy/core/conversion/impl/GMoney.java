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
 * Created by creatorfromhell on 5/11/2019.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class GMoney extends Converter {
  private File saveFile = new File("plugins/gMoney/money.yml");
  private FileConfiguration save = YamlConfiguration.loadConfiguration(saveFile);

  private File configFile = new File("plugins/gMoney/config.yml");
  private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
  @Override
  public String name() {
    return "gMoney";
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {

    final ConfigurationSection accountSection = save.getConfigurationSection("money");
    if(accountSection != null) {
      final Set<String> accounts = accountSection.getKeys(false);
      for(String uuid : accounts) {
        Currency cur = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
        Converter.convertedAdd(uuid, HellConomy.instance().getDefaultWorld(), cur.name(), new BigDecimal(save.getString("money." + uuid)));
      }
    }
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);

    final String table = config.getString("MySQL.Table");

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT * FROM " + table + ";")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        Converter.convertedAdd(results.getString("player"),
            HellConomy.instance().getDefaultWorld(), currency.name(),
            new BigDecimal(results.getDouble("money")));
      }
    } catch(SQLException ignore) {}
    db.close();
  }
}