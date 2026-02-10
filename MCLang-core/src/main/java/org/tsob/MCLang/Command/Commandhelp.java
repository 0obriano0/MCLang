package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tsob.MCLang.DataBase.DataBase;

public class Commandhelp extends mainCommandSystem{

  public Commandhelp() {
    super(  "help",
        DataBase.fileMessage.getString("Command.Help.help"),
        new ArrayList<String>(Arrays.asList("mclang.user.help")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) throws Exception {
    sender.sendMessage(" ");
    sender.sendMessage("=============== MCLang ===============");
    sender.sendMessage(" ");
    
    // 使用註冊系統取得所有指令
    for (ImainCommandSystem cmd : RegistryCommandSystem.getAllCommands()) {
      if (cmd.hasPermission(sender)) {
        sender.sendMessage(cmd.getHelp());
      }
    }
    
    sender.sendMessage(" ");
    sender.sendMessage("===========================================");
  }
  
  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    run((CommandSender)player, commandLabel, command,args);
  }
}
