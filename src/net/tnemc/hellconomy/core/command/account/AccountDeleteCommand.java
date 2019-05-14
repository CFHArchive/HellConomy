package net.tnemc.hellconomy.core.command.account;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import net.tnemc.hellconomy.core.common.account.IDStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
public class AccountDeleteCommand extends TNECommand {
  public AccountDeleteCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "delete";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "del", "remove", "rmv", "-"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.account.delete";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public String getHelp() {
    return "messages.commands.account.delete";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {

    if(arguments.length < 1) {
      help(sender);
      return false;
    }

    HellConomy.instance().saveManager().open();
    if(!HellConomy.api().hasAccount(arguments[0])) {
      sender.sendMessage(ChatColor.RED + "Invalid account name specified \"" + arguments[0] + "\".");
      HellConomy.instance().saveManager().close();
      return false;
    }

    final UUID id = HellAPI.getID(arguments[0]);
    HellAccount.delete(id);
    IDStorage.delete(id);

    sender.sendMessage(ChatColor.GOLD + "Successfully deleted the account for \"" + arguments[0] + "\".");
    HellConomy.instance().saveManager().close();
    return true;
  }
}