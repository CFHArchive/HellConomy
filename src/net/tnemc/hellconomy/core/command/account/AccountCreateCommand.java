package net.tnemc.hellconomy.core.command.account;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
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
public class AccountCreateCommand extends TNECommand {
  public AccountCreateCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "create";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "+", "make", "new"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.account.create";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public String getHelp() {
    return "messages.commands.account.create";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {

    if(arguments.length < 1) {
      help(sender);
      return false;
    }

    HellConomy.instance().saveManager().open();
    if(HellConomy.api().hasAccount(arguments[0])) {
      sender.sendMessage(ChatColor.RED + "Account with the name \"" + arguments[0] + "\" already exists.");
      HellConomy.instance().saveManager().close();
      return false;
    }

    final String world = (sender instanceof Player)? getPlayer(sender).getWorld().getName() : HellConomy.instance().getDefaultWorld();

    BigDecimal amount = BigDecimal.ZERO;
    if(arguments.length > 1) {
      try {
        amount = new BigDecimal(arguments[1]);
      } catch(Exception e) {
        HellConomy.instance().saveManager().close();
        sender.sendMessage(ChatColor.RED + "Amount must be a valid decimal.");
        return false;
      }
    }

    final UUID id = HellAPI.getID(arguments[0]);
    HellConomy.api().createAccount(id);
    HellAccount.initializeHoldings(id, HellConomy.instance().normalizeWorld(world));

    if(arguments.length > 1) {
      HellConomy.api().setHoldings(id.toString(), amount);
    }

    sender.sendMessage(ChatColor.GOLD + "Successfully created the account for \"" + arguments[0] + "\".");
    HellConomy.instance().saveManager().close();
    return true;
  }
}