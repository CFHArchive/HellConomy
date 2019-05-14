package net.tnemc.hellconomy.core.command.money;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import net.tnemc.hellconomy.core.currency.CurrencyFormatter;
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
public class MoneyOtherCommand extends TNECommand {
  public MoneyOtherCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "other";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "see", "o"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.other";
  }

  @Override
  public boolean console() {
    return false;
  }

  @Override
  public String getHelp() {
    return "messages.commands.money.other";
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

    String world = (sender instanceof Player)? getPlayer(sender).getWorld().getName() : HellConomy.instance().getDefaultWorld();
    if(arguments.length > 1) {
      world = arguments[1];
    }

    final UUID id = HellAPI.getID(arguments[0]);
    final BigDecimal amount = HellAccount.getHoldings(id, world, HellConomy.currencyManager().get(world), false);
    sender.sendMessage(ChatColor.YELLOW + arguments[0] + " has " + CurrencyFormatter.format(HellConomy.currencyManager().get(world), world, amount));
    HellConomy.instance().saveManager().close();
    return true;
  }
}