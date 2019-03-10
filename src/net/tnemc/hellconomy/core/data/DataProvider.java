package net.tnemc.hellconomy.core.data;

/**
 * Created by creatorfromhell on 3/18/2018.
 * <p>
 * HellConomy Minecraft Server Plugin
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public interface DataProvider {

  String getDriver();
  Boolean dataSource();
  String dataSourceURL();
  String getURL(String file, String host, int port, String database);

  default void preConnect(String file, String host, int port, String database) {

  }
}