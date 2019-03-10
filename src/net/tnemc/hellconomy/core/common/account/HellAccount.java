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
@Table("hellco_accounts")
@CompositePK({"account_id"})
public class HellAccount extends Model {

  //account_id
  //account_display
  //account_created
  //account_player

  public static boolean add(UUID id, String display, long created, boolean player) {
    boolean exists = exists(id);
    HellAccount account = (exists)? getAccount(id) : new HellAccount();

    if(!exists) account.set("account_id", freeID().toString());
    account.set("account_display", display);
    if(!exists) account.set("account_created", created);
    if(!exists) account.set("account_player", player);

    return account.saveIt();
  }

  public static HellAccount getAccount(UUID id) {
    return HellAccount.findFirst("account_id = ?", id.toString());
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

  public static boolean exists(UUID id) {
    return HellAccount.findFirst("account_id = ?", id.toString()) != null;
  }

  public static void delete(final UUID id) {
    HellAccount.delete("village_id = ?", id.toString());
  }
}