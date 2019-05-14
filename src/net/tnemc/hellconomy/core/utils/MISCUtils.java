package net.tnemc.hellconomy.core.utils;

import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.util.Set;
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
public class MISCUtils {
  /**
   * @return Whether the bukkit in use is for MC >= 1.8
   */
  public static boolean isOneEight() {
    return Bukkit.getVersion().contains("1.8") || isOneNine() || isOneTen() || isOneEleven() || isOneTwelve();
  }

  /**
   * @return Whether the bukkit in use is for MC >= 1.9
   */
  public static boolean isOneNine() {
    return Bukkit.getVersion().contains("1.9") || isOneTen() || isOneEleven() || isOneTwelve();
  }

  /**
   * @return Whether the bukkit in use is for MC >= 1.10
   */
  public static boolean isOneTen() {
    return Bukkit.getVersion().contains("1.10") || isOneEleven() || isOneTwelve();
  }

  /**
   * @return Whether the bukkit in use is for MC >= 1.11
   */
  public static boolean isOneEleven() {
    return Bukkit.getVersion().contains("1.11") || isOneTwelve();
  }

  /**
   * @return Whether the bukkit in use is for MC >= 1.12
   */
  public static boolean isOneTwelve() {
    return Bukkit.getVersion().contains("1.12");
  }

  public static boolean isOneThirteen() {
    return Bukkit.getVersion().contains("1.13");
  }

  public static boolean offHand() {
    return isOneNine() || isOneTen() || isOneEleven() || isOneTwelve() || isOneThirteen();
  }

  public static void restore(CommandSender sender) {
    File file = new File(HellConomy.instance().getDataFolder(), "extracted.yml");
    YamlConfiguration original = YamlConfiguration.loadConfiguration(file);
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    Set<String> accounts = configuration.getConfigurationSection("Accounts").getKeys(false);

    HellConomy.instance().saveManager().open();
    accounts.forEach((username) -> {
      String reformattedUsername = username.replaceAll("\\!", ".").replaceAll("\\@", "-").replaceAll("\\%", "_");
      if(reformattedUsername.equalsIgnoreCase("server account")) reformattedUsername = "Server_Account";
      UUID id = HellAPI.getID(reformattedUsername);
      HellConomy.api().getOrCreate(id);
      Set<String> worlds = configuration.getConfigurationSection("Accounts." + username + ".Balances").getKeys(false);
      worlds.forEach((world) -> {
        Set<String> currencies = configuration.getConfigurationSection("Accounts." + username + ".Balances." + world).getKeys(false);
        currencies.forEach((currency) -> {
          String finalCurrency = (currency.equalsIgnoreCase("default"))? HellConomy.currencyManager().get(world).name() : currency;
          String balance = original.getString("Accounts." + username + ".Balances." + world + "." + currency);
          HellConomy.api().setHoldings(id.toString(), new BigDecimal(balance), HellConomy.currencyManager().get(world, finalCurrency), world);
        });
      });
    });
    HellConomy.instance().saveManager().open();

    sender.sendMessage(ChatColor.WHITE + "Restored accounts from extracted.yml.");
  }
}