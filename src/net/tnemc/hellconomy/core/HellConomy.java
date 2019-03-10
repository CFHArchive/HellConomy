package net.tnemc.hellconomy.core;

import net.tnemc.hellconomy.core.command.CommandManager;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.common.configuration.ConfigurationMapper;
import net.tnemc.hellconomy.core.compatibility.ItemCompatibility;
import net.tnemc.hellconomy.core.compatibility.item.ItemCompatibility12;
import net.tnemc.hellconomy.core.compatibility.item.ItemCompatibility13;
import net.tnemc.hellconomy.core.data.SaveManager;
import net.tnemc.hellconomy.core.data.Version;
import net.tnemc.hellconomy.core.utils.MISCUtils;
import net.tnemc.hellconomy.core.utils.Metrics;
import net.tnemc.hellconomy.core.world.WorldEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Daniel.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class HellConomy extends JavaPlugin {

  private static HellConomy instance;

  private SaveManager saveManager;
  private CommandManager commandManager;
  private String defaultWorld = "world";
  private String version = "0.1.0.0";

  private ConfigurationMapper mapper;
  private WorldManager worldManager;

  //Compatibility Classes.
  private ItemCompatibility itemCompatibility;

  public void onLoad() {
    instance = this;
  }

  public void onEnable() {

    mapper = new ConfigurationMapper();
    mapper.initialize();

    worldManager = new WorldManager();

    mapper.getConfiguration("world_sharing").getKeys().forEach((world)->{
      final List<String> shared = mapper.getConfiguration("world_sharing").getStringList("world_sharing." + world);

      worldManager.addEntry(new WorldEntry(world, world));
      for(String worldName : shared) {
        worldManager.addEntry(new WorldEntry(worldName, world));
      }
    });

    commandManager = new CommandManager();

    if(Bukkit.getWorlds().size() >= 1) {
      defaultWorld = Bukkit.getServer().getWorlds().get(0).getName();
    }

    saveManager = new SaveManager();

    saveManager.createTables();
    saveManager.open();
    if(Version.informationExists()) {
      if(Version.outdated()) {
        saveManager.updateTables(version);
        Version.add(version);
      }
    }
    saveManager.close();

    //TODO: Register Commands.

    //TODO: Register Listeneners
    itemCompatibility = (MISCUtils.isOneThirteen())? new ItemCompatibility13() : new ItemCompatibility12();

    new Metrics(this);

    getLogger().info("HellConomy has been enabled!");
  }

  public void onDisable() {

    getLogger().info("HellConomy has been disabled!");
  }

  private CommandManager getCommandManager() {
    return commandManager;
  }

  public void registerCommand(String[] accessors, TNECommand command) {
    commandManager.commands.put(accessors, command);
    commandManager.registerCommands();
  }

  public void registerCommands(Map<String[], TNECommand> commands) {
    commandManager.commands = commands;
    commandManager.registerCommands();
  }

  public void registerListener(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }

  public void unregisterCommand(String[] accessors) {
    commandManager.unregister(accessors);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
    TNECommand ecoCommand = commandManager.Find(label);
    if(ecoCommand != null) {
      if(!ecoCommand.canExecute(sender)) {
        sender.sendMessage(ChatColor.RED + "I'm sorry, but you're not allowed to use that command.");
        return false;
      }
      return ecoCommand.execute(sender, label, arguments);
    }
    return false;
  }

  public static void debug(StackTraceElement[] stack) {
    for(StackTraceElement element : stack) {
      logger().warning(element.toString());
    }
  }

  public SaveManager saveManager() {
    return saveManager;
  }

  public String getVersion() {
    return version;
  }

  public String getDefaultWorld() {
    return defaultWorld;
  }

  public static Logger logger() {
    return instance.getServer().getLogger();
  }

  public static HellConomy instance() {
    return instance;
  }

  public static ConfigurationMapper mapper() {
    return instance.mapper;
  }

  public static WorldManager worldManager() {
    return instance.worldManager;
  }

  public static ItemCompatibility item() {
    return instance().itemCompatibility;
  }
}