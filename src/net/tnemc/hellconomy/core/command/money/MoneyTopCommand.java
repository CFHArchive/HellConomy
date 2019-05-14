package net.tnemc.hellconomy.core.command.money;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.account.Balance;
import net.tnemc.hellconomy.core.currency.CurrencyFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Paginator;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MoneyTopCommand extends TNECommand {
  public MoneyTopCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "top";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "high"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.top";
  }

  @Override
  public boolean console() {
    return false;
  }

  @Override
  public String getHelp() {
    return "messages.commands.money.pay";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    final String world = getPlayer(sender).getWorld().getName();

    HellConomy.instance().saveManager().open();
    final Paginator p = Balance.getBalTop(world);

    long page = 1;

    if(arguments.length > 0) {
      try {
        page = Long.parseLong(arguments[0]);
      } catch(Exception ignore) {

      }
    }

    if(page > p.pageCount()) page = p.pageCount();

    LazyList<Balance> entries = p.getPage((int)page);
    sender.sendMessage(ChatColor.GOLD + "~~~ HellConomy Top " + page + "/ " + p.pageCount() + " ~~~");
    for(Balance balance : entries) {
      sender.sendMessage(ChatColor.WHITE + HellAPI.getUsername(balance.getString("balance_owner")) + ChatColor.GOLD + " - " + ChatColor.WHITE + CurrencyFormatter.format(world, balance.getBigDecimal("balance_amount")));
      //sender.sendMessage(ChatColor.WHITE + balance.getString("balance_owner") + ChatColor.GOLD + " - " + ChatColor.WHITE + CurrencyFormatter.format(world, balance.getBigDecimal("balance_amount")));
    }

    HellConomy.instance().saveManager().close();
    return true;
  }
}