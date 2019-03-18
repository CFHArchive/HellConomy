package net.tnemc.hellconomy.core.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.data.impl.H2;
import net.tnemc.hellconomy.core.data.impl.MySQL;
import org.javalite.activejdbc.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by creatorfromhell on 3/18/2018.
 * <p>
 * HellConomy Minecraft Server Plugin
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class SaveManager {

  private Map<String, List<String>> dataTables = new HashMap<>();
  private Map<String, DataProvider> providers = new HashMap<>();

  //Instances
  private DB db = new DB("HellConomy");
  private final HikariConfig config;
  private final HikariDataSource dataSource;

  private String type;

  private String file;
  private String host;
  private int port;
  private String dbName;
  private String user;
  private String pass;

  public SaveManager() {
    type = HellConomy.mapper().getString("database.type").toLowerCase();
    file = new File(HellConomy.instance().getDataFolder(), HellConomy.mapper().getString("database.file"))
           .getAbsolutePath();
    host = HellConomy.mapper().getString("database.host");
    port = HellConomy.mapper().getInt("database.port");
    dbName = HellConomy.mapper().getString("database.db");
    user = HellConomy.mapper().getString("database.user");
    pass = HellConomy.mapper().getString("database.password");

    //Initialize DataProviders.
    providers.put("mysql", new MySQL());
    providers.put("h2", new H2());

    config = new HikariConfig();

    if(providers.get(type).dataSource()) {
      config.setDataSourceClassName(providers.get(type).dataSourceURL());
    } else {
      config.setDriverClassName(providers.get(type).getDriver());
      config.setJdbcUrl(providers.get(type).getURL(file, host, port, dbName));
    }

    config.setUsername(user);
    config.setPassword(pass);

    if(type.equalsIgnoreCase("mysql")) {
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    dataSource = new HikariDataSource(config);

    //initialize tables.
    List<String> mysql = new ArrayList<>();

    mysql.add("CREATE TABLE IF NOT EXISTS `hellco_version` (" +
                  "`id` INTEGER NOT NULL UNIQUE," +
                  "`version_value` VARCHAR(15)" +
                  ") ENGINE = INNODB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");

    mysql.add("CREATE TABLE IF NOT EXISTS `hellco_version` (" +
                  "`id` INTEGER NOT NULL UNIQUE," +
                  "`version_value` VARCHAR(15)" +
                  ") ENGINE = INNODB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
    
    List<String> h2 = new ArrayList<>();

    h2.add("CREATE TABLE IF NOT EXISTS `hellco_version` (" +
                  "`id` INTEGER NOT NULL UNIQUE," +
                  "`version_value` VARCHAR(15)" +
                  ") ENGINE = INNODB;");

    dataTables.put("mysql", mysql);
    dataTables.put("h2", h2);
  }

  public void createTables() {
    List<String> tables = dataTables.get(type);
    try {
      DataProvider provider = providers.get(type);
      provider.preConnect(file, host, port, dbName);
      db.open(provider.getDriver(), provider.getURL(file, host, port, dbName), user, pass);
      tables.forEach((table)->{
        db.exec(table);
      });
      Version.add(HellConomy.instance().getVersion());
    } finally {
      db.close();
    }
  }

  public void updateTables(String version) {

  }

  public void open() {
    try {
      db.open(dataSource);
    } catch(Exception ignore) {
    }
  }

  public void close() {
    db.close();
  }

  public void addProvider(String type, DataProvider provider) {
    providers.put(type, provider);
  }

  public void registerTables(String type, List<String> tables) {
    dataTables.put(type, tables);
  }
}