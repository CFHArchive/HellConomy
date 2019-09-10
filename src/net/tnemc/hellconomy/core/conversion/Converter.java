package net.tnemc.hellconomy.core.conversion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.conversion.impl.AdvancedEconomy;
import net.tnemc.hellconomy.core.conversion.impl.BConomy;
import net.tnemc.hellconomy.core.conversion.impl.BEconomy;
import net.tnemc.hellconomy.core.conversion.impl.BOSEconomy;
import net.tnemc.hellconomy.core.conversion.impl.BasicEconomy;
import net.tnemc.hellconomy.core.conversion.impl.Blings;
import net.tnemc.hellconomy.core.conversion.impl.CMI;
import net.tnemc.hellconomy.core.conversion.impl.DevCoinSystem;
import net.tnemc.hellconomy.core.conversion.impl.ECEconomy;
import net.tnemc.hellconomy.core.conversion.impl.EasyCoins;
import net.tnemc.hellconomy.core.conversion.impl.EcoPlugin;
import net.tnemc.hellconomy.core.conversion.impl.EcoSystem;
import net.tnemc.hellconomy.core.conversion.impl.EconomyAPI;
import net.tnemc.hellconomy.core.conversion.impl.Essentials;
import net.tnemc.hellconomy.core.conversion.impl.FeConomy;
import net.tnemc.hellconomy.core.conversion.impl.FeatherEconomy;
import net.tnemc.hellconomy.core.conversion.impl.GMoney;
import net.tnemc.hellconomy.core.conversion.impl.GemsEconomy;
import net.tnemc.hellconomy.core.conversion.impl.Meep;
import net.tnemc.hellconomy.core.conversion.impl.Meller;
import net.tnemc.hellconomy.core.conversion.impl.MineCoin;
import net.tnemc.hellconomy.core.conversion.impl.MineCoinsYML;
import net.tnemc.hellconomy.core.conversion.impl.MineConomy;
import net.tnemc.hellconomy.core.conversion.impl.MinetopiaEconomy;
import net.tnemc.hellconomy.core.conversion.impl.MoConomy;
import net.tnemc.hellconomy.core.conversion.impl.RealEconomy;
import net.tnemc.hellconomy.core.conversion.impl.SQLConomy;
import net.tnemc.hellconomy.core.conversion.impl.SaneEconomy;
import net.tnemc.hellconomy.core.conversion.impl.SimpleConomy;
import net.tnemc.hellconomy.core.conversion.impl.SimplisticEconomy;
import net.tnemc.hellconomy.core.conversion.impl.SwiftEconomy;
import net.tnemc.hellconomy.core.conversion.impl.TokensEconomy;
import net.tnemc.hellconomy.core.conversion.impl.TownyEco;
import net.tnemc.hellconomy.core.conversion.impl.XConomy;
import net.tnemc.hellconomy.core.conversion.impl.iConomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javalite.activejdbc.DB;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public abstract class Converter {
  protected String usedFile = HellConomy.mapper().getString("conversion.file");
  protected String mysqlHost = HellConomy.mapper().getString("conversion.options.host");
  protected Integer mysqlPort = HellConomy.mapper().getInt("conversion.options.port");
  protected String mysqlDatabase = HellConomy.mapper().getString("conversion.options.database");
  protected String mysqlUser = HellConomy.mapper().getString("conversion.options.user");
  protected String mysqlPassword = HellConomy.mapper().getString("conversion.options.password");

  protected String type = HellConomy.mapper().getString("conversion.format");
  protected DB db;
  private final HikariConfig config;
  protected final HikariDataSource dataSource;

  public Converter() {
    db = new DB("Conversion");

    config = new HikariConfig();

    if(HellConomy.instance().saveManager().getProviders().get(type).dataSource()) {
      config.setDataSourceClassName(HellConomy.instance().saveManager().getProviders().get(type).dataSourceURL());
    } else {
      config.setDriverClassName(HellConomy.instance().saveManager().getProviders().get(type).getDriver());
      config.setJdbcUrl(HellConomy.instance().saveManager().getProviders().get(type).getURL(usedFile, mysqlHost, mysqlPort, mysqlDatabase));
    }

    config.setUsername(mysqlUser);
    config.setPassword(mysqlPassword);

    if(type.equalsIgnoreCase("mysql")) {
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    dataSource = new HikariDataSource(config);
  }

  public abstract String name();

  public void convert() {
    try {
      new File(HellConomy.instance().getDataFolder(), "extracted.yml").createNewFile();
    } catch(Exception ignore) {
    }
    try {
      switch (type.toLowerCase()) {
        case "mysql":
          mysql();
          break;
        case "sqlite":
          sqlite();
          break;
        case "h2":
          h2();
          break;
        case "postgre":
          postgre();
          break;
        case "flatfile":
          flatfile();
          break;
        case "mini":
          flatfile();
          break;
        case "json":
          json();
          break;
        case "yaml":
          yaml();
          break;
        case "inventory":
          inventoryDB();
          break;
        case "experience":
          expDB();
          break;
      }
    } catch(InvalidDatabaseImport exception) {
      HellConomy.instance().getLogger().log(Level.WARNING, exception.getMessage());
    }
  }

  public void mysql() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("MySQL", name());
  }

  public void sqlite() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("SQLite", name());
  }

  public void h2() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("H2", name());
  }

  public void postgre() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("PostgreSQL", name());
  }

  public void flatfile() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("FlatFile", name());
  }

  public void yaml() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("YAML", name());
  }

  //This is the dumbest trend ever.
  public void json() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("JSON", name());
  }

  //iConomy Specific
  public void inventoryDB() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("InventoryDB", name());
  }

  //iConomy Specific.
  public void expDB() throws InvalidDatabaseImport {
    throw new InvalidDatabaseImport("ExperienceDB", name());
  }

  public static void convertedAdd(String identifier, String world, String currency, BigDecimal amount) {
    File conversionFile = new File(HellConomy.instance().getDataFolder(), "extracted.yml");
    FileConfiguration conversion = YamlConfiguration.loadConfiguration(conversionFile);

    BigDecimal starting = BigDecimal.ZERO;

    String newID = identifier.replaceAll("\\.", "!").replaceAll("\\-", "@").replaceAll("\\_", "%");

    if(conversion.contains("Accounts." + newID + ".Balances." + world + "." + currency)) {
      starting = new BigDecimal(conversion.getString("Accounts." + newID + ".Balances." + world + "." + currency));
    }

    conversion.set("Accounts." + newID + ".Balances." + world + "." + currency, starting.add(amount).toPlainString());
    try {
      conversion.save(conversionFile);
    } catch (IOException ignore) {
    }
  }

  public static Converter getConverter(String name) {

    switch(name.toLowerCase()) {
      case "advancedeconomy":
        return new AdvancedEconomy();
      case "basiceconomy":
        return new BasicEconomy();
      case "bconomy":
        return new BConomy();
      case "beconomy":
        return new BEconomy();
      case "blings":
        return new Blings();
      case "boseconomy":
        return new BOSEconomy();
      case "cmi":
        return new CMI();
      case "devcoinsystem":
        return new DevCoinSystem();
      case "easycoins":
        return new EasyCoins();
      case "ececonomy":
        return new ECEconomy();
      case "economyapi":
        return new EconomyAPI();
      case "ecoplugin":
        return new EcoPlugin();
      case "ecosystem":
        return new EcoSystem();
      case "essentials":
        return new Essentials();
      case "feathereconomy":
        return new FeatherEconomy();
      case "feconomy":
        return new FeConomy();
      case "gmoney":
        return new GMoney();
      case "gemseconomy":
        return new GemsEconomy();
      case "iconomy":
        return new iConomy();
      case "meep":
        return new Meep();
      case "meller":
        return new Meller();
      case "minecoin":
        return new MineCoin();
      case "minecoinsyml":
        return new MineCoinsYML();
      case "mineconomy":
        return new MineConomy();
      case "minetopia":
        return new MinetopiaEconomy();
      case "moconomy":
        return new MoConomy();
      case "realeconomy":
        return new RealEconomy();
      case "saneeconomy":
        return new SaneEconomy();
      case "simpleconomy":
        return new SimpleConomy();
      case "simplisticeconomy":
        return new SimplisticEconomy();
      case "sqlconomy":
        return new SQLConomy();
      case "swifteconomy":
        return new SwiftEconomy();
      case "tokenseconomy":
        return new TokensEconomy();
      case "townyeco":
        return new TownyEco();
      case "xconomy":
        return new XConomy();
    }
    return null;
  }
}