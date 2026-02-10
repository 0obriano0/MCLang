package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tsob.MCLang.DataBase.DataBase;

public abstract class mainCommandSystem implements ImainCommandSystem {
  private final transient String id;
  private final transient List<String> permissions;
  private final transient String help;
  private boolean rundefault;

  /**
   * 定義一個指令
   * 
   * @param id          指令ID
   * @param help        說明敘述
   * @param permissions 權限(type : List of String)
   */
  protected mainCommandSystem(final String id, final String help, final List<String> permissions) {
    this.id = id;
    this.help = help;
    this.permissions = permissions;
  }

  @Override
  public String getName() {
    return id;
  }

  @Override
  public String getHelp() {
    return help;
  }

  @Override
  public List<String> getPermissions() {
    return permissions;
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    if (!(sender instanceof Player))
      return true;
    else
      return hasPermission((Player) sender);
  }

  @Override
  public boolean hasPermission(Player player) {
    if (player.isOp())
      return true;
    for (String per : permissions) {
      if (!player.hasPermission(per))
        return false;
    }
    return true;
  }

  @Override
  public void rundefault() {
    rundefault = true;
  }

  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args,
      final ClassLoader classLoader) throws Exception {
    run(sender, commandLabel, command, args);
    if (rundefault) {
      if (getsubCommands().size() > 0) {

        if (args.length == 0 && RegistrySubCommandSystem.hasSubCommand(id, "help")) {
          ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, "help");
          if (!cmd.hasPermission(sender)) {
            sender.sendMessage(DataBase.fileMessage.getString("Command.NoPermission"));
          }
          try {
            cmd.run(sender, commandLabel + ".help", command, args, classLoader);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (args.length != 0 && (commandLabel.equalsIgnoreCase(DataBase.pluginName.toLowerCase() + "." + id)
            || commandLabel.equalsIgnoreCase("mclang." + id) || commandLabel.equalsIgnoreCase("mdop." + id))) {
          if (!RegistrySubCommandSystem.hasSubCommand(id, args[0])) {
            sender.sendMessage(DataBase.fileMessage.getString("Command.CanNotFind"));
          } else {
            if (args.length >= 1) {
              String[] newargs = Arrays.copyOfRange(args, 1, args.length);
              ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, args[0]);
              try {
                cmd.run(sender, commandLabel + "." + args[0], command, newargs, classLoader);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        }
      } else {
        sender.sendMessage(DataBase.fileMessage.getString("Command.CmdCanNotUse"));
      }
    }
  }

  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) throws Exception {
    rundefault();
  }

  @Override
  public void run(Player player, String commandLabel, Command command, String[] args, final ClassLoader classLoader)
      throws Exception {
    run(player, commandLabel, command, args);
    if (rundefault) {
      if (getsubCommands().size() > 0) {

        if (args.length == 0 && RegistrySubCommandSystem.hasSubCommand(id, "help")) {
          ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, "help");
          if (!cmd.hasPermission(player)) {
            DataBase.sendMessage(player, DataBase.fileMessage.getString("Command.NoPermission"));
          }
          try {
            cmd.run(player, commandLabel + ".help", command, args, classLoader);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (args.length != 0 && (commandLabel.equalsIgnoreCase(DataBase.pluginName.toLowerCase() + "." + id)
            || commandLabel.equalsIgnoreCase("mclang." + id) || commandLabel.equalsIgnoreCase("mdop." + id))) {
          if (!RegistrySubCommandSystem.hasSubCommand(id, args[0])) {
            DataBase.sendMessage(player, DataBase.fileMessage.getString("Command.CanNotFind"));
          } else {
            if (args.length >= 1) {
              String[] newargs = Arrays.copyOfRange(args, 1, args.length);
              ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, args[0]);
              if (!cmd.hasPermission(player)) {
                DataBase.sendMessage(player, DataBase.fileMessage.getString("Command.NoPermission"));
              }
              try {
                cmd.run(player, commandLabel + "." + args[0], command, newargs, classLoader);
              } catch (Exception e) {
                if (DataBase.getDebug())
                  e.printStackTrace();
              }
            }
          }
        }
      } else {
        // 預留
      }
    }
  }

  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    rundefault();
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String commandLabel, Command command, String[] args,
      final ClassLoader classLoader) {
    if (getsubCommands().size() == 0)
      return Collections.emptyList();

    if (args.length == 1) {
      List<String> show_commands = new ArrayList<String>();
      for (String key : getsubCommands()) {
        ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, key);
        if (key.indexOf(args[0].toLowerCase()) != -1 && cmd.hasPermission(sender))
          show_commands.add(key);
      }
      return show_commands;
    } else if (args.length > 1 && RegistrySubCommandSystem.hasSubCommand(id, args[0])) {
      ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, args[0]);
      String[] newargs = Arrays.copyOfRange(args, 1, args.length);
      try {
        return cmd.tabComplete(sender, commandLabel + "." + args[0], command, newargs, classLoader);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return Collections.emptyList();
    }
    return Collections.emptyList();
  }

  @Override
  public List<String> tabComplete(Player player, String commandLabel, Command command, String[] args,
      final ClassLoader classLoader) {
    if (getsubCommands().size() == 0)
      return Collections.emptyList();

    if (args.length == 1) {
      List<String> show_commands = new ArrayList<String>();
      for (String key : getsubCommands()) {
        ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, key);
        if (key.indexOf(args[0].toLowerCase()) != -1 && cmd.hasPermission(player))
          show_commands.add(key);
      }
      return show_commands;
    } else if (args.length > 1 && RegistrySubCommandSystem.hasSubCommand(id, args[0])) {
      ImainCommandSystem cmd = RegistrySubCommandSystem.getSubCommand(id, args[0]);
      String[] newargs = Arrays.copyOfRange(args, 1, args.length);
      if (!cmd.hasPermission(player))
        return Collections.emptyList();
      try {
        return cmd.tabComplete(player, commandLabel + "." + args[0], command, newargs, classLoader);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return Collections.emptyList();
    }
    return Collections.emptyList();
  }

  @Override
  public List<String> getsubCommands() {
    return RegistrySubCommandSystem.getSubCommandNames(id);
  }

  @Override
  public void onDisable() {
    if (getsubCommands().size() > 0) {
      RegistrySubCommandSystem.clearSubCommandsForParent(id);
    }
  }
}
