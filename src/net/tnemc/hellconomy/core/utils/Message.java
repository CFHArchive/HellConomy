package net.tnemc.hellconomy.core.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class Message {

  public static final Map<String, String> colours;
  static {
    colours = new HashMap<>();
    //Colour Characters
    colours.put("<aqua>", ChatColor.AQUA.toString());
    colours.put("<black>", ChatColor.BLACK.toString());
    colours.put("<blue>", ChatColor.BLUE.toString());
    colours.put("<dark_aqua>", ChatColor.DARK_AQUA.toString());
    colours.put("<dark_blue>", ChatColor.DARK_BLUE.toString());
    colours.put("<dark_gray>", ChatColor.GRAY.toString());
    colours.put("<dark_grey>", ChatColor.GRAY.toString());
    colours.put("<dark_green>", ChatColor.DARK_GREEN.toString());
    colours.put("<dark_purple>", ChatColor.DARK_PURPLE.toString());
    colours.put("<dark_red>", ChatColor.DARK_RED.toString());
    colours.put("<gold>", ChatColor.GOLD.toString());
    colours.put("<gray>", ChatColor.GRAY.toString());
    colours.put("<grey>", ChatColor.GRAY.toString());
    colours.put("<green>", ChatColor.GREEN.toString());
    colours.put("<purple>", ChatColor.LIGHT_PURPLE.toString());
    colours.put("<red>", ChatColor.RED.toString());
    colours.put("<white>", ChatColor.WHITE.toString());
    colours.put("<yellow>", ChatColor.YELLOW.toString());

    //Special Characters
    colours.put("<magic>", ChatColor.MAGIC.toString());
    colours.put("<bold>", ChatColor.BOLD.toString());
    colours.put("<strike>", ChatColor.STRIKETHROUGH.toString());
    colours.put("<underline>", ChatColor.UNDERLINE.toString());
    colours.put("<italic>", ChatColor.ITALIC.toString());
    colours.put("<reset>", ChatColor.RESET.toString());
  }

  private HashMap<String, String> variables = new HashMap<String, String>();
  private String node;

  public Message(String node) {
    this.node = node;
  }

  public void addVariable(String variable, String replacement) {
    variables.put(variable, replacement);
  }

  public static String replaceColours(String message, boolean strip) {
    Iterator<Map.Entry<String, String>> it = colours.entrySet().iterator();

    while(it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      String replacement = (strip)? "" : entry.getValue();
      message = message.replace(entry.getKey(), replacement);
    }
    if(strip) {
      return ChatColor.stripColor(message);
    }
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String grab() {

    String message = this.node;
    Iterator<Map.Entry<String, String>> it = variables.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      message = message.replace(entry.getKey(), entry.getValue());
    }
    return message;
  }

  public String[] grabWithNew(CommandSender sender) {

    String[] message = new String[] { this.node };

    String[] formatted = new String[message.length];

    for(int i = 0; i < message.length; i++) {
      String send = message[i];
      if (!send.equals(this.node)) {
        Iterator<Map.Entry<String, String>> it = variables.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> entry = it.next();
          send = send.replace(entry.getKey(), entry.getValue());
        }
      }
      Boolean strip = !(sender instanceof Player);
      formatted[i] = replaceColours(send, strip);
    }
    return formatted;
  }

  public void translate(CommandSender sender) {
    if(sender == null) return;

    String[] message = new String[] { this.node };
    for(String s : message) {
      String send = s;
      if (!send.equals(this.node)) {
        Iterator<Map.Entry<String, String>> it = variables.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> entry = it.next();
          send = send.replace(entry.getKey(), entry.getValue());
        }
      }
      Boolean strip = !(sender instanceof Player);
      sender.sendMessage(replaceColours(send, strip));
    }
  }
}