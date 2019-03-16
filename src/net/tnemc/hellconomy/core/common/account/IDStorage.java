package net.tnemc.hellconomy.core.common.account;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
@DbName("HellConomy")
@Table("hellco_ids")
@CompositePK({"uuid"})
public class IDStorage extends Model {

  public static boolean add(UUID identifier, String display) {
    final boolean exists = exists(identifier);
    IDStorage storage = (exists)? getStorage(identifier) : new IDStorage();

    if(!exists) storage.set("uuid", identifier.toString());
    storage.set("display", display);

    return storage.saveIt();
  }

  public static IDStorage getStorage(UUID identifier) {
    return IDStorage.findFirst("uuid = ?", identifier.toString());
  }

  public static IDStorage getStorage(String display) {
    return IDStorage.findFirst("display = ?", display);
  }

  public static boolean exists(UUID identifier) {
    return IDStorage.findFirst("uuid = ?", identifier.toString()) != null;
  }

  public static boolean exists(String display) {
    return IDStorage.findFirst("display = ?", display) != null;
  }

  public static UUID getID(String display) {
    return UUID.fromString(getStorage(display).getString("uuid"));
  }

  public static UUID freeID() {
    UUID id = UUID.randomUUID();
    if(exists(id)) {
      //should never happen.
      while(exists(id)) {
        id = UUID.randomUUID();
      }
    }
    return id;
  }
}