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
public class MoneySetCommand extends TNECommand {
  public MoneySetCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "set";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "="
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.money.set";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    return false;
  }
}