package org.tsob.MCLang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.tsob.MCLang.Command.ImainCommandSystem;
import org.tsob.MCLang.Command.ToolCommandSystem;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.DataBase.UpdateChecker;

public class Main extends JavaPlugin {
  public static Plugin plugin;
  public static Server server;
  public static Metrics metrics;

  @Override
  public void onEnable() {
    plugin = this;
    server = this.getServer();

    saveDefaultConfig();
    reloadConfig();

    setEvents();
    DataBase.fileMessage.reloadFile();
    DataBase.fileMinecraftLang.reloadWithLangAndVersion(Main.plugin.getConfig().getString("lang"));
    
    // bStats 統計初始化
    int pluginId = 26149; // 你的 bStats plugin ID
    metrics = new Metrics(this, pluginId);

    new UpdateChecker(this).start();
  }

  @Override
  public void onDisable() {

  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return Command(sender, command, label, args, Main.class.getClassLoader(),
        DataBase.mainJavaPathDot + ".Command");
  }

  public boolean Command(CommandSender sender, Command command, String commandLabel, String[] args,
      final ClassLoader classLoader, final String commandPath) {
    if (args.length == 0) {
      ImainCommandSystem cmd = ToolCommandSystem.getCommandClass("help", classLoader, commandPath);
      if (!cmd.hasPermission(sender)) {
        sender.sendMessage(DataBase.fileMessage.getString("Command.NoPermission"));
        return false;
      }
      try {
        cmd.run(sender, commandLabel + ".help", command, args, classLoader, commandPath);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (args.length != 0 && (commandLabel.equalsIgnoreCase(DataBase.pluginName.toLowerCase())
        || commandLabel.equalsIgnoreCase("mclang"))) {
      if (!DataBase.getCommands(plugin).contains(args[0])) {
        sender.sendMessage(DataBase.fileMessage.getString("Command.CanNotFind"));
        return false;
      }
      if (args.length >= 1) {
        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        ImainCommandSystem cmd = ToolCommandSystem.getCommandClass(args[0], classLoader, commandPath);
        if ((sender instanceof Player)) {
          if (!cmd.hasPermission(sender)) {
            sender.sendMessage(DataBase.fileMessage.getString("Command.NoPermission"));
            return false;
          }
          try {
            cmd.run((Player) sender, commandLabel + "." + args[0], command, newargs, classLoader, commandPath);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          try {
            cmd.run(sender, commandLabel + "." + args[0], command, newargs, classLoader, commandPath);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    return onTabComplete(sender, cmd, label, args, Main.class.getClassLoader(),
        DataBase.mainJavaPathDot + DataBase.pluginName + ".Command");
  }

  public List<String> onTabComplete(CommandSender sender, Command command, String commandLabel, String[] args,
      final ClassLoader classLoader, final String commandPath) {
    if (args.length == 1) {
      List<String> show_commands = new ArrayList<String>();
      for (String key : DataBase.getCommands(plugin)) {
        ImainCommandSystem cmd = ToolCommandSystem.getCommandClass(key, classLoader, commandPath);
        if (key.indexOf(args[0].toLowerCase()) != -1 && cmd.hasPermission(sender))
          show_commands.add(key);
      }
      return show_commands;
    } else if (args.length > 1 && DataBase.getCommands(plugin).contains(args[0])) {
      ImainCommandSystem cmd = ToolCommandSystem.getCommandClass(args[0], classLoader, commandPath);
      String[] newargs = Arrays.copyOfRange(args, 1, args.length);
      if ((sender instanceof Player)) {
        if (!cmd.hasPermission(sender))
          return Collections.emptyList();
        try {
          return cmd.tabComplete((Player) sender, commandLabel + "." + args[0], command, newargs, classLoader,
              commandPath);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        try {
          return cmd.tabComplete(sender, commandLabel + "." + args[0], command, newargs, classLoader, commandPath);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return Collections.emptyList();
    }
    return Collections.emptyList();
  }

  /**
   * 設定 server listener
   */
  private void setEvents() {
    // Bukkit.getServer().getPluginManager().registerEvents(new ShopListener(),
    // this);

  }

  /**
   * 重新讀取 資料
   */
  public static void reload() {
    plugin.reloadConfig();

    DataBase.fileMessage.reloadFile();
    DataBase.fileMinecraftLang.reloadWithLangAndVersion(Main.plugin.getConfig().getString("lang"));
  }
}
