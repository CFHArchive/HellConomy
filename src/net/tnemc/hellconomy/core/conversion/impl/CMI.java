package net.tnemc.hellconomy.core.conversion.impl;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.conversion.Converter;
import net.tnemc.hellconomy.core.conversion.InvalidDatabaseImport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.javalite.activejdbc.Base.close;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class CMI extends Converter {

  private File configFile = new File(HellConomy.instance().getDataFolder(), "../CMI/config.yml");
  private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

  @Override
  public String name() {
    return "CMI";
  }

  @Override
  public void mysql() throws InvalidDatabaseImport {

    final String prefix = config.getString("mysql.tablePrefix");
    final String table = prefix + "users";

    db.open(dataSource);
    try(Connection connection = db.getConnection();
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT username, Balance FROM " + table + ";")) {

      final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
      while(results.next()) {
        Converter.convertedAdd(results.getString("username"),
                               HellConomy.instance().getDefaultWorld(), currency.name(),
                                      new BigDecimal(results.getDouble("Balance")));
      }
    } catch(SQLException ignore) {}
    close();

  }

  @Override
  public void sqlite() throws InvalidDatabaseImport {
    try {

      Class.forName("org.sqlite.JDBC");

      try(Connection connection = DriverManager.getConnection("jdbc:sqlite:" + new File(HellConomy.instance().getDataFolder(), "../CMI/cmi.sqlite.db").getAbsolutePath());
          Statement statement = connection.createStatement();
          ResultSet results = statement.executeQuery("SELECT username, Balance FROM users;")) {

        final Currency currency = HellConomy.currencyManager().get(HellConomy.instance().getDefaultWorld());
        while(results.next()) {
          Converter.convertedAdd(results.getString("username"),
                                 HellConomy.instance().getDefaultWorld(), currency.name(),
                                        new BigDecimal(results.getDouble("Balance")));
        }
      } catch(SQLException ignore) {}

    } catch(Exception ignore) {}

  }
}