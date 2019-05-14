package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;

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
public class EasyCoins extends Converter {
  private File dataDirectory = new File(HellConomy.instance().getDataFolder(), "../EasyCoins/players");

  @Override
  public String name() {
    return "EasyCoins";
  }

  @Override
  public void yaml() throws InvalidDatabaseImport {
    if(!dataDirectory.isDirectory() || dataDirectory.listFiles() == null || dataDirectory.listFiles().length == 0) return;

    for(File accountFile : dataDirectory.listFiles()) {
      FileConfiguration acc = YamlConfiguration.loadConfiguration(accountFile);

      Double money = acc.contains("coins")? acc.getDouble("coins") : 0.0;
      String currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld()).name();

      Converter.convertedAdd(accountFile.getName().replace(".yml", ""), HellConomy.instance().getDefaultWorld(), currency, new BigDecimal(money));
    }
  }
}