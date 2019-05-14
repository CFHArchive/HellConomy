package net.tnemc.hellconomy.core.command.convert;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.conversion.Converter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
public class ConvertCommand extends TNECommand {
  public ConvertCommand(HellConomy plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "convert";
  }

  @Override
  public String[] getAliases() {
    return new String[0];
  }

  @Override
  public String getNode() {
    return "hellconomy.command.convert";
  }

  @Override
  public boolean console() {
    return true;
  }

  @Override
  public String getHelp() {
    return "/convert <from> - Converts all data from plugin <from>.";
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    if(arguments.length >= 1) {
      final String old = arguments[0];
      final Converter converter = Converter.getConverter(old);
      if(converter == null) {
        sender.sendMessage(ChatColor.RED + "Invalid <from> argument specified.");
        return false;
      }
      Bukkit.getScheduler().runTaskAsynchronously(HellConomy.instance(), ()->{
        converter.convert();
        sender.sendMessage(ChatColor.WHITE + "Conversion has completed. Running restoration command.");
        Bukkit.getScheduler().runTask(HellConomy.instance(), ()->Bukkit.getServer().dispatchCommand(sender, "account import"));
      });
      sender.sendMessage(ChatColor.WHITE + "Conversion is now in progress.");
      return true;
    }
    help(sender);
    return false;
  }
}