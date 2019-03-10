package net.tnemc.hellconomy.core.data.impl;

import net.tnemc.hellconomy.core.data.DataProvider;

/**
 * Created by creatorfromhell on 3/18/2018.
 * <p>
 * HellConomy Minecraft Server Plugin
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MySQL implements DataProvider {
  @Override
  public String getDriver() {
    return "com.mysql.jdbc.Driver";
  }

  @Override
  public Boolean dataSource() {
    return false;
  }

  @Override
  public String dataSourceURL() {
    return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
  }

  @Override
  public String getURL(String file, String host, int port, String database) {
    return "jdbc:mysql://" + host + ":" + port + "/" +
           database + "?useSSL=false&rewriteBatchedStatements=true&useServerPrepStmts=true";
  }
}
