package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.DataBase.DataBase;

public class Commandhelp extends mainCommandSystem{

  public Commandhelp() {
    super(  "help",
        "/mclang help 取得指令說明",
        new ArrayList<String>(Arrays.asList("mclang.user.help")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) throws Exception {
    sender.sendMessage(" ");
    sender.sendMessage("=============== MCLang ===============");
    sender.sendMessage(" ");
    for(String command_value :DataBase.getCommands(Main.plugin)) {
      ImainCommandSystem cmd = ToolCommandSystem.getCommandClass(command_value);
      if(cmd.hasPermission(sender))
        sender.sendMessage(cmd.getHelp());
    }
    sender.sendMessage(" ");
    sender.sendMessage("===========================================");
  }
  
  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    run((CommandSender)player, commandLabel, command,args);
  }
}
