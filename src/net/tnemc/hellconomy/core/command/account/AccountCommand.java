package net.tnemc.hellconomy.core.command.account;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class AccountCommand extends TNECommand {
  public AccountCommand(HellConomy plugin) {
    super(plugin);

    subCommands.add(new AccountCreateCommand(plugin));
    subCommands.add(new AccountDeleteCommand(plugin));
    subCommands.add(new AccountImportCommand(plugin));
  }

  @Override
  public String getName() {
    return "account";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "acc"
    };
  }

  @Override
  public String getNode() {
    return "";
  }

  @Override
  public boolean console() {
    return true;
  }
}