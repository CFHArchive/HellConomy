package net.tnemc.hellconomy.core.command.money;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.account.HellAccount;
import net.tnemc.hellconomy.core.currency.CurrencyFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
public class MoneyBalanceCommand extends TNECommand {
  public MoneyBalanceCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "balance";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "bal", "value", "val"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.balance";
  }

  @Override
  public boolean console() {
    return false;
  }

  @Override
  public String getHelp() {
    return "messages.commands.money.balance";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {

    final UUID id = getPlayer(sender).getUniqueId();
    final String world = HellConomy.instance().normalizeWorld(getPlayer(sender).getWorld().getName());
    final BigDecimal amount = HellAccount.getHoldings(id, world, HellConomy.currencyManager().get(world), false);
    sender.sendMessage(ChatColor.YELLOW + "You have " + CurrencyFormatter.format(HellConomy.currencyManager().get(world), world, amount));
    HellConomy.instance().saveManager().close();
    return true;
  }
}