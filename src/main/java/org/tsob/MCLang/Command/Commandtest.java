package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
public class Commandtest extends mainCommandSystem{
  public Commandtest() {
    super(  "test",
        "/MCLang test 取得指令說明",
        new ArrayList<String>(Arrays.asList("mclang.admin.test")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args){

  }
}
  
