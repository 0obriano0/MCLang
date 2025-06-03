package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command418 extends mainCommandSystem{

  public Command418() {
    super(  "418",
        "/mclang 418 你可以試試看這個指令",
        new ArrayList<String>(Arrays.asList("mclang.user.418")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) throws Exception {
    sender.sendMessage("I'm a clay pot");
  }
  
  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    run((CommandSender)player, commandLabel, command,args);
  }
}
