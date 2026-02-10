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
import org.tsob.MCLang.Command.RegistryCommandSystem;
import org.tsob.MCLang.Command.Commandhelp;
import org.tsob.MCLang.Command.Commandpath;
import org.tsob.MCLang.Command.Commandreload;
import org.tsob.MCLang.Command.ImainCommandSystem;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.DataBase.UpdateChecker;
import org.tsob.MCLang.Platform.SchedulerFactory;
import org.tsob.MCLang.API.ApiMainBase;

public class Main extends JavaPlugin {
  public static Plugin plugin;
  public static Server server;
  public static Metrics metrics;
  private ApiMainBase apiMainInstance = null;

  @Override
  public void onEnable() {
    plugin = this;
    server = this.getServer();

    saveDefaultConfig();
    reloadConfig();

    // 註冊所有指令
    registerCommands();

    setEvents();
    DataBase.fileMessage.reloadFile();
    DataBase.fileMinecraftLang.reloadWithLangAndVersion(Main.plugin.getConfig().getString("lang"));

    // bStats 統計初始化
    int pluginId = 26149; // 你的 bStats plugin ID
    metrics = new Metrics(this, pluginId);

    new UpdateChecker(this).start();

    DataBase.Print("MCLang Scheduler Mode: " + (SchedulerFactory.isFolia() ? "Folia" : "Spigot/Paper"));
    
    // 嘗試加載和啟用 API 模組
    loadAndEnableApiModule();
  }

  @Override
  public void onDisable() {
    // 停用 API 模組
    if (apiMainInstance != null) {
      try {
        apiMainInstance.onDisable();
      } catch (Exception e) {
        DataBase.Print("停用 API 模組時發生錯誤: " + e.getMessage());
      }
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return Command(sender, command, label, args, Main.class.getClassLoader());
  }

  public boolean Command(CommandSender sender, Command command, String commandLabel, String[] args,
      final ClassLoader classLoader) {
    if (args.length == 0) {
      // 顯示 help 指令
      ImainCommandSystem cmd = RegistryCommandSystem.getCommand("help");
      if (cmd == null) {
        sender.sendMessage("§cHelp 指令未註冊！");
        return false;
      }
      if (!cmd.hasPermission(sender)) {
        sender.sendMessage(DataBase.fileMessage.getString("Command.NoPermission"));
        return false;
      }
      try {
        cmd.run(sender, commandLabel + ".help", command, args, classLoader);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }

    if (commandLabel.equalsIgnoreCase(DataBase.pluginName.toLowerCase())
        || commandLabel.equalsIgnoreCase("mclang")) {

      String subCommand = args[0];

      // 檢查指令是否存在
      if (!RegistryCommandSystem.hasCommand(subCommand)) {
        sender.sendMessage(DataBase.fileMessage.getString("Command.CanNotFind"));
        return false;
      }

      String[] newargs = Arrays.copyOfRange(args, 1, args.length);
      ImainCommandSystem cmd = RegistryCommandSystem.getCommand(subCommand);

      if (sender instanceof Player) {
        if (!cmd.hasPermission(sender)) {
          sender.sendMessage(DataBase.fileMessage.getString("Command.NoPermission"));
          return false;
        }
        try {
          cmd.run((Player) sender, commandLabel + "." + subCommand, command, newargs, classLoader);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        try {
          cmd.run(sender, commandLabel + "." + subCommand, command, newargs, classLoader);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    return onTabComplete(sender, cmd, label, args, Main.class.getClassLoader());
  }

  /**
   * 處理 Tab 自動完成
   * 
   * @param sender      指令發送者
   * @param command     指令物件
   * @param alias       指令別名
   * @param args        指令參數
   * @param classLoader 類別載入器
   * @return Tab 補全列表
   */
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args,
      final ClassLoader classLoader) {
    if (args.length == 1) {
      // 第一層：列出所有可用的子指令
      List<String> completions = new ArrayList<>();
      String input = args[0].toLowerCase();

      for (String cmdName : RegistryCommandSystem.getCommandNames()) {
        ImainCommandSystem cmd = RegistryCommandSystem.getCommand(cmdName);
        if (cmd != null && cmd.hasPermission(sender) && cmdName.toLowerCase().startsWith(input)) {
          completions.add(cmdName);
        }
      }

      Collections.sort(completions);
      return completions;
    } else if (args.length >= 2) {
      // 第二層及以上：交給各個指令自己處理
      String subCommand = args[0];
      ImainCommandSystem cmd = RegistryCommandSystem.getCommand(subCommand);

      if (cmd != null && cmd.hasPermission(sender)) {
        String[] newargs = Arrays.copyOfRange(args, 1, args.length);

        if (sender instanceof Player) {
          return cmd.tabComplete((Player) sender, alias + "." + subCommand, command, newargs, classLoader);
        } else {
          return cmd.tabComplete(sender, alias + "." + subCommand, command, newargs, classLoader);
        }
      }
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
   * 註冊所有指令
   */
  private void registerCommands() {
    RegistryCommandSystem.clearAll(); // 清除舊的註冊

    // 在這裡手動註冊所有指令
    RegistryCommandSystem.registerCommands(
        new Commandhelp(),
        new Commandreload(),
        new Commandpath()
    // 在這裡新增更多指令...
    );

    // DataBase.Print("已註冊 " + RegistryCommandSystem.getCommandNames().size() + " 個指令");
  }

  /**
   * 重新讀取 資料
   */
  public static void reload() {
    Main mainInstance = (Main) plugin;
    plugin.reloadConfig();

    DataBase.fileMessage.reloadFile();
    DataBase.fileMinecraftLang.reloadWithLangAndVersion(Main.plugin.getConfig().getString("lang"));

    // 清除並重新註冊指令
    mainInstance.registerCommands();
    
    // 重新加載 API 模組, 這邊有註冊指令的話需要重新註冊
    if (mainInstance.apiMainInstance != null) {
      try {
        mainInstance.apiMainInstance.onReload(); // 假設 API 模組有 onReload 方法
      } catch (Exception e) {
        DataBase.Print("重新加載 API 模組時發生錯誤: " + e.getMessage());
      }
    }
  }

  /**
   * 動態加載和啟用 API 模組
   */
  private void loadAndEnableApiModule() {
    try {
      // 嘗試加載 API 模組中的 apiMain 類
      Class<?> apiMainClass = Class.forName("org.tsob.MCLang.API.apiMain");
      
      // 檢查是否是抽象類
      if (!java.lang.reflect.Modifier.isAbstract(apiMainClass.getModifiers())) {
        // 創建實例
        apiMainInstance = (ApiMainBase) apiMainClass.getDeclaredConstructor().newInstance();
        
        // 調用 onEnable
        apiMainInstance.onEnable();
        
        // DataBase.Print("成功加載 API 模組");
      } else {
        // DataBase.Print("API 模組的 apiMain 是抽象類，跳過加載");
      }
    } catch (ClassNotFoundException e) {
      // API 模組不存在，這是正常情況
      // DataBase.Print("未找到 API 模組，僅使用核心功能");
    } catch (Exception e) {
      DataBase.Print("加載 API 模組時發生錯誤: " + e.getMessage());
      if (DataBase.getDebug()) {
        e.printStackTrace();
      }
    }
  }
}
