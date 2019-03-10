package net.tnemc.hellconomy.core.command.money;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;
import org.bukkit.command.CommandSender;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MoneyTakeCommand extends TNECommand {
  public MoneyTakeCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "take";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "remove", "-"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.take";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    return true;
  }
}