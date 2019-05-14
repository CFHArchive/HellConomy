package net.tnemc.hellconomy.core.command.account;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.utils.MISCUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class AccountImportCommand extends TNECommand {
  public AccountImportCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "import";
  }

  @Override
  public String[] getAliases() {
    return new String[] {
        "imp"
    };
  }

  @Override
  public String getNode() {
    return "hellconomy.account.import";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public String getHelp() {
    return "/account import - Import account information from your extracted.yml file.";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    File file = new File(HellConomy.instance().getDataFolder(), "extracted.yml");
    if(file.exists()) {
      Bukkit.getScheduler().runTaskAsynchronously(HellConomy.instance(), () -> MISCUtils.restore(sender));
      return true;
    }
    sender.sendMessage(ChatColor.RED + "Unable to locate extracted.yml file for restoration.");
    return false;
  }
}