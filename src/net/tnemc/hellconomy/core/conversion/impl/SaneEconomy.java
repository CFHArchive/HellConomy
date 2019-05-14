package net.tnemc.hellconomy.core.conversion.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
public class SaneEconomy extends Converter {
  private File configFile = new File("plugins/SaneEconomy/config.yml");
  private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

  @Override
  public String name() {
    return "SaneEconomy";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {
    db.open(dataSource);

    try(ResultSet results = db.getConnection().createStatement().executeQuery("SELECT * FROM saneeconomy_balances;")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        Converter.convertedAdd(results.getString("unique_identifier"),
            HellConomy.instance().getDefaultWorld(), currency.name(),
            new BigDecimal(results.getDouble("balance")));
      }
    } catch(SQLException ignore) {}
    db.close();
  }

  @Override
  public void json() throws InvalidDatabaseImport {
    File file = new File("plugins/SaneEconomy" + config.getString("backend.file", "economy.json"));
    final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    Map<String, Double> balances = new HashMap<>();
    try {
      balances = (HashMap<String, Double>)gson.fromJson(new FileReader(file), new TypeToken<Map<String, Double>>(){}.getType());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
    HellConomy.instance().saveManager().open();
    balances.forEach((id, balance)->{
      Converter.convertedAdd(HellAPI.getUsername(id), HellConomy.instance().getDefaultWorld(), currency.name(), new BigDecimal(balance));
    });
    HellConomy.instance().saveManager().close();
  }
}