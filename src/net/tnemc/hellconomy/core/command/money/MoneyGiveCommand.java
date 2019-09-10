package net.tnemc.hellconomy.core.command.money;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.currency.CurrencyFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MoneyGiveCommand extends TNECommand {
  public MoneyGiveCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "give";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "add", "+"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.give";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public String getHelp() {
    return "messages.commands.money.give";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {

    if(arguments.length < 2) {
      help(sender);
      return false;
    }

    HellConomy.instance().saveManager().open();
    if(!HellConomy.api().hasAccount(arguments[0])) {
      sender.sendMessage(ChatColor.RED + "Invalid account name specified \"" + arguments[0] + "\".");
      HellConomy.instance().saveManager().close();
      return false;
    }

    if(HellConomy.mapper().getBool("account.receive_perm")) {
      final Player player = Bukkit.getPlayer(arguments[0]);
      if(player == null || !player.hasPermission("hellconomy.money.receive")) {
        sender.sendMessage(ChatColor.RED + "Account \"" + arguments[0] + "\" unable to receive money.");
        HellConomy.instance().saveManager().close();
        return false;
      }
    }

    BigDecimal amount = BigDecimal.ZERO;
    try {
      amount = new BigDecimal(arguments[1]);
    } catch(Exception e) {
      HellConomy.instance().saveManager().close();
      sender.sendMessage(ChatColor.RED + "Amount must be a valid decimal.");
      return false;
    }

    String world = (sender instanceof Player)? getPlayer(sender).getWorld().getName() : HellConomy.instance().getDefaultWorld();
    if(arguments.length > 2) {
      world = arguments[2];
    }

    HellConomy.api().addHoldings(arguments[0], amount, world);
    sender.sendMessage(ChatColor.GOLD + "Successfully gave \"" + arguments[0] + "\" " + CurrencyFormatter.format(world, amount) + ".");
    HellConomy.instance().saveManager().close();
    return true;
  }
}