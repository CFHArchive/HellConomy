package net.tnemc.hellconomy.core;

import net.milkbowl.vault.economy.Economy;
import net.tnemc.hellconomy.core.api.Economy_HellConomy;
import net.tnemc.hellconomy.core.api.HellAPI;
import net.tnemc.hellconomy.core.command.CommandManager;
import net.tnemc.hellconomy.core.command.TNECommand;
import net.tnemc.hellconomy.core.command.account.AccountCommand;
import net.tnemc.hellconomy.core.command.money.MoneyCommand;
import net.tnemc.hellconomy.core.common.configuration.ConfigurationMapper;
import net.tnemc.hellconomy.core.compatibility.ItemCompatibility;
import net.tnemc.hellconomy.core.compatibility.item.ItemCompatibility12;
import net.tnemc.hellconomy.core.compatibility.item.ItemCompatibility13;
import net.tnemc.hellconomy.core.data.SaveManager;
import net.tnemc.hellconomy.core.data.Version;
import net.tnemc.hellconomy.core.listeners.ConnectionListener;
import net.tnemc.hellconomy.core.listeners.EntityPortalListener;
import net.tnemc.hellconomy.core.listeners.PlayerJoinListener;
import net.tnemc.hellconomy.core.listeners.PlayerQuitListener;
import net.tnemc.hellconomy.core.listeners.TeleportListener;
import net.tnemc.hellconomy.core.listeners.WorldChangeListener;
import net.tnemc.hellconomy.core.listeners.WorldLoadListener;
import net.tnemc.hellconomy.core.utils.MISCUtils;
import net.tnemc.hellconomy.core.utils.Metrics;
import net.tnemc.hellconomy.core.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
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
  private Map<String, WorldManager> worldManagers = new HashMap<>();

  private SaveManager saveManager;
  private CommandManager commandManager;
  private String defaultWorld = "world";
  private String version = "0.1.0.0";
  private String server = "Main Server";

  private ConfigurationMapper mapper;

  //API Classes
  private Economy_HellConomy vaultEconomy;
  private HellAPI api;

  //Compatibility Classes.
  private ItemCompatibility itemCompatibility;
  private CurrencyManager currencyManager;

  private boolean blacklisted = false;

  public void onLoad() {
    instance = this;

    if(getServer().getPluginManager().getPlugin("GUIShop") != null) {
      getLogger().info("Unable to load HellConomy as it is incompatible with GUIShop.");
      blacklisted = true;
      return;
    }

    getLogger().info("Loading HellConomy with Java Version: " + System.getProperty("java.version"));
    instance = this;
    api = new HellAPI(this);

    //Initialize Economy Classes
    if(getServer().getPluginManager().getPlugin("Vault") != null) {
      vaultEconomy = new Economy_HellConomy(this);
      setupVault();
    }
  }

  public void onEnable() {

    if(blacklisted) {
      getLogger().info("Server Blacklisted.");
      return;
    }

    getDataFolder().mkdir();

    mapper = new ConfigurationMapper();
    mapper.initialize();

    System.out.println("Config Null: " + (mapper.getConfigurationByID("world_sharing") == null));

    mapper.getConfigurationByID("world_sharing").getSection("world_sharing").getKeys().forEach((world)->{
      final List<String> shared = mapper.getConfigurationByID("world_sharing").getStringList("world_sharing." + world);

      addWorldManager(new WorldManager(world, world));
      for(String worldName : shared) {
        addWorldManager(new WorldManager(worldName, world));
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

    //Register Commands.
    registerCommand(new String[]{"account"}, new AccountCommand(this));
    registerCommand(new String[]{"money"}, new MoneyCommand(this));

    //Register Listeneners
    registerListener(new ConnectionListener(this));
    registerListener(new EntityPortalListener(this));
    registerListener(new PlayerJoinListener(this));
    registerListener(new PlayerQuitListener(this));
    registerListener(new TeleportListener(this));
    registerListener(new WorldLoadListener(this));
    registerListener(new WorldChangeListener(this));

    itemCompatibility = (MISCUtils.isOneThirteen())? new ItemCompatibility13() : new ItemCompatibility12();

    server = mapper.getString("server.name");

    currencyManager = new CurrencyManager();

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

  public static String getServerName() {
    return instance.server;
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

  public static ItemCompatibility item() {
    return instance().itemCompatibility;
  }

  public static CurrencyManager currencyManager() {
    return instance.currencyManager;
  }


  public void addWorldManager(WorldManager manager) {
    worldManagers.put(manager.getWorld(), manager);
  }

  public boolean hasWorldManager(String world) {
    return worldManagers.containsKey(world);
  }

  public WorldManager getWorldManager(String world) {
    for(WorldManager manager : this.worldManagers.values()) {
      if(manager.getWorld().equalsIgnoreCase(world)) {
        return manager;
      }
    }
    return worldManagers.get(defaultWorld);
  }

  public Collection<WorldManager> getWorldManagers() {
    return worldManagers.values();
  }

  public Map<String, WorldManager> getWorldManagersMap() {
    return worldManagers;
  }

  public String normalizeWorld(String world) {
    if(!HellConomy.mapper().getBool("server.multi_world")) {
      return HellConomy.instance().getDefaultWorld();
    }

    if(worldManagers.containsKey(world)) return getWorldManager(world).getBalanceWorld();
    return world;
  }

  public static HellAPI api() {
    return instance.api;
  }

  private void setupVault() {
    getServer().getServicesManager().register(Economy.class, vaultEconomy, this, ServicePriority.Highest);
    getLogger().info("Hooked into Vault");
  }
}